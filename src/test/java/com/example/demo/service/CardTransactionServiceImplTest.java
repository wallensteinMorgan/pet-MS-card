package com.example.demo.service;


import com.example.demo.dto.CardDTO;
import com.example.demo.entity.CardEntity;
import com.example.demo.service.impl.CardTransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardTransactionServiceImplTest {
    @Mock
    private CardService cardService;
    @InjectMocks
    private CardTransactionServiceImpl cardTransactionService;

    private CardEntity cardEntity;
    private CardDTO cardDTO;

    @BeforeEach
    void setUp() {
        cardEntity = new CardEntity();
        cardEntity.setId(1L);
        cardEntity.setBalance(new BigDecimal("100.00"));

        cardDTO = new CardDTO();
        cardDTO.setId(1L);
    }

    @Test
        // баланс карты увеличивается при поступлении
    void processReceiving_WhenAmountWasIncreased_ShouldIncreaseBalance() {
        BigDecimal amountToAdd = new BigDecimal("50.00");
        BigDecimal expectedBalance = cardEntity.getBalance().add(amountToAdd);

        CardDTO updatedCardDTO = new CardDTO();
        updatedCardDTO.setId(1L);
        updatedCardDTO.setBalance(expectedBalance);

        when(cardService.updateCardById(eq(1L), any())).thenReturn(updatedCardDTO);

        when(cardService.getCardById(1L)).thenReturn(updatedCardDTO);

        cardTransactionService.processReceiving(1L, amountToAdd);

        assertEquals(expectedBalance, cardService.getCardById(1L).getBalance());

        verify(cardService, times(1)).updateCardById(eq(1L), any());
    }

    @Test
        //баланс карты уменьшается, когда средства списываются с карты
    void processPayment_WhenBalanceIsSufficient_ShouldReduceBalance() {
        BigDecimal amountToPay = new BigDecimal("50.00");
        BigDecimal initialBalance = new BigDecimal("100.00");
        BigDecimal expectedBalance = initialBalance.subtract(amountToPay);

        CardDTO updatedCardDTO = new CardDTO();
        updatedCardDTO.setId(1L);
        updatedCardDTO.setBalance(expectedBalance);

        when(cardService.getCardById(eq(1L))).thenReturn(updatedCardDTO);

        when(cardService.updateCardById(eq(1L), any())).thenAnswer(invocation -> {
            Consumer<CardEntity> consumer = invocation.getArgument(1);
            CardEntity cardEntity = new CardEntity();
            cardEntity.setBalance(initialBalance);
            consumer.accept(cardEntity);
            return null;
        });

        cardTransactionService.processPayment(1L, amountToPay);

        assertEquals(expectedBalance, cardService.getCardById(1L).getBalance());

        verify(cardService, times(1)).updateCardById(eq(1L), any());
    }

    @Test
        //выбрасывается исключение, когда баланс карты недостаточен для оплаты
    void processPayment_WhenBalanceIsInsufficient_ShouldThrowException() {
        Long cardId = 1L;
        BigDecimal amountToPay = new BigDecimal("150.00");

        when(cardService.updateCardById(eq(cardId), any())).thenAnswer(invocation -> {
            Consumer<CardEntity> update = invocation.getArgument(1);
            update.accept(cardEntity);
            return cardEntity;
        });

        Exception ex = assertThrows(RuntimeException.class, () -> {
            cardTransactionService.processPayment(cardId, amountToPay);
        });
        assertEquals("Not enough money on card " + cardId, ex.getMessage(),
                "Недостаточно средств");
    }

    @Test
//оба баланса карт обновляются при успешном переводе средств между картами
    void processTransfer_WhenAmountIsTransferred_ShouldUpdateBothCards() {
        Long fromId = 1L;
        Long toId = 2L;
        BigDecimal amountToTransfer = new BigDecimal("50.00");

        BigDecimal fromCardBalanceAfterPayment = new BigDecimal("100.00").subtract(amountToTransfer);
        BigDecimal toCardBalanceAfterReceiving = new BigDecimal("200.00").add(amountToTransfer);

        CardDTO fromCardDTO = new CardDTO();
        fromCardDTO.setId(fromId);
        fromCardDTO.setBalance(fromCardBalanceAfterPayment);

        CardDTO toCardDTO = new CardDTO();
        toCardDTO.setId(toId);
        toCardDTO.setBalance(toCardBalanceAfterReceiving);

        when(cardService.updateCardById(eq(fromId), any())).thenReturn(fromCardDTO);  // Мокаем updateCardById для карты отправителя
        when(cardService.updateCardById(eq(toId), any())).thenReturn(toCardDTO);  // Мокаем updateCardById для карты получателя

        when(cardService.getCardById(fromId)).thenReturn(fromCardDTO);
        when(cardService.getCardById(toId)).thenReturn(toCardDTO);

        cardTransactionService.processTransfer(fromId, toId, amountToTransfer);  // Вызываем метод processTransfer

        assertEquals(fromCardBalanceAfterPayment, cardService.getCardById(fromId).getBalance());  // Проверяем баланс отправителя
        assertEquals(toCardBalanceAfterReceiving, cardService.getCardById(toId).getBalance());  // Проверяем баланс получателя

        verify(cardService, times(1)).updateCardById(eq(fromId), any());
        verify(cardService, times(1)).updateCardById(eq(toId), any());
    }
    @Test
        // если при переводе возникла ошибка на стороне получателя,
        // то операция откатывается и баланс отправителя не изменяется
    void processTransfer_WhenReceivingFails_ShouldRollbackPayment() {
        Long fromId = 1L;
        Long toId = 2L;
        BigDecimal amountToTransfer = new BigDecimal("50.00");

        CardDTO fromCardDTO = new CardDTO();
        fromCardDTO.setId(fromId);
        fromCardDTO.setBalance(new BigDecimal("100.00"));

        CardDTO toCardDTO = new CardDTO();
        toCardDTO.setId(toId);
        toCardDTO.setBalance(new BigDecimal("200.00"));

        when(cardService.updateCardById(eq(fromId), any())).thenReturn(fromCardDTO);

        when(cardService.updateCardById(eq(toId), any())).thenThrow(new RuntimeException("Receiving error"));

        when(cardService.getCardById(fromId)).thenReturn(fromCardDTO);
        when(cardService.getCardById(toId)).thenReturn(toCardDTO);

        assertThrows(RuntimeException.class, () -> cardTransactionService.processTransfer(fromId, toId, amountToTransfer));  // Ожидаем исключение

        assertEquals(new BigDecimal("100.00"), cardService.getCardById(fromId).getBalance());

        assertEquals(new BigDecimal("200.00"), cardService.getCardById(toId).getBalance());

        verify(cardService, times(1)).updateCardById(eq(fromId), any());
        verify(cardService, times(1)).updateCardById(eq(toId), any());
    }
    @Test
        //если при переводе с карты возникла ошибка, то баланс карты получателя не изменяется
    void processTransfer_WhenPaymentFails_ShouldNotUpdateReceivingCard() {
        Long fromId = 1L;
        Long toId = 2L;
        BigDecimal amountToTransfer = new BigDecimal("50.00");

        BigDecimal fromCardBalanceAfterPayment = new BigDecimal("100.00").subtract(amountToTransfer);
        BigDecimal toCardBalanceAfterReceiving = new BigDecimal("200.00").add(amountToTransfer);

        CardDTO fromCardDTO = new CardDTO();
        fromCardDTO.setId(fromId);
        fromCardDTO.setBalance(fromCardBalanceAfterPayment);

        CardDTO toCardDTO = new CardDTO();
        toCardDTO.setId(toId);
        toCardDTO.setBalance(toCardBalanceAfterReceiving);

        when(cardService.updateCardById(eq(fromId), any())).thenReturn(fromCardDTO);
        when(cardService.updateCardById(eq(toId), any())).thenReturn(toCardDTO);

        when(cardService.getCardById(fromId)).thenReturn(fromCardDTO);
        when(cardService.getCardById(toId)).thenReturn(toCardDTO);

        cardTransactionService.processTransfer(fromId, toId, amountToTransfer);

        assertEquals(fromCardBalanceAfterPayment, cardService.getCardById(fromId).getBalance());
        assertEquals(toCardBalanceAfterReceiving, cardService.getCardById(toId).getBalance());
        verify(cardService, times(1)).updateCardById(eq(fromId), any());
        verify(cardService, times(1)).updateCardById(eq(toId), any());
    }
}
