package com.example.demo.service;


import com.example.demo.dto.CardDTO;
import com.example.demo.dto.CardNotificationEvent;
import com.example.demo.entity.CardEntity;
import com.example.demo.exception.BaseTransactionException;
import com.example.demo.service.impl.CardTransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

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
    @Mock
    private KafkaTemplate<String, CardNotificationEvent> kafkaTemplate;
    @InjectMocks
    private CardTransactionServiceImpl cardTransactionService;

    private CardEntity cardEntity;
    private CardDTO cardDTO;

    @BeforeEach
    void setUp() {
        cardEntity = new CardEntity();
        cardEntity.setId(1L);
        cardEntity.setBalance(new BigDecimal("100.00"));
        cardEntity.setCardNumber("1234567891234567");
        cardDTO = new CardDTO();
        cardDTO.setId(1L);
        cardDTO.setBalance(new BigDecimal("100.00"));
        cardDTO.setCardNumber("1234567891234567");
    }
    @Test // зачисление на карту
    void processReceiving_ShouldIncreaseBalanceAndSendEvent(){
        BigDecimal amount = new BigDecimal("50.00");
        Long userId = 1L;

        when(cardService.getCardByUserId(userId)).thenReturn(cardDTO);

        cardTransactionService.processReceiving(userId, amount);

        ArgumentCaptor<Consumer<CardEntity>> captor = ArgumentCaptor.forClass(Consumer.class);
        verify(cardService).updateCardById(eq(1L), captor.capture());

        Consumer<CardEntity> updateConsumer = captor.getValue();
        CardEntity entityToUpdate = new CardEntity();
        entityToUpdate.setBalance(new BigDecimal("100.00"));
        updateConsumer.accept(entityToUpdate);
        assertEquals(new BigDecimal("150.00"), entityToUpdate.getBalance());

        verify(kafkaTemplate).send(eq("card-notifications"), any(CardNotificationEvent.class));
    }


    @Test
        //списание с карты, если денег хватает
    void processPayment_WhenBalanceIsSufficient_ShouldReduceBalance() {
        BigDecimal amountToPay = new BigDecimal("50.00");
        Long userId = 1L;

        when(cardService.getCardByUserId(userId)).thenReturn(cardDTO);
        cardTransactionService.processPayment(userId, amountToPay);

        ArgumentCaptor<Consumer<CardEntity>> captor = ArgumentCaptor.forClass(Consumer.class);
        verify(cardService).updateCardById(eq(1L), captor.capture());

        Consumer<CardEntity> updateConsumer = captor.getValue();
        CardEntity entityToUpdate = new CardEntity();
        entityToUpdate.setBalance(new BigDecimal("100.00"));
        updateConsumer.accept(entityToUpdate);
        assertEquals(new BigDecimal("50.00"), entityToUpdate.getBalance());

        verify(kafkaTemplate).send(eq("card-notifications"), any(CardNotificationEvent.class));
    }
    @Test
        //выбрасывается исключение, когда баланс карты недостаточен для оплаты
    void processPayment_WhenBalanceIsInsufficient_ShouldThrowException() {
        BigDecimal amount = new BigDecimal("150.00");
        Long userId = 1L;

        when(cardService.getCardByUserId(userId)).thenReturn(cardDTO);

        assertThrows(BaseTransactionException.class, ()-> {
            cardTransactionService.processPayment(userId, amount);
        });
        verify(cardService, never()).updateCardById(any(), any());
        verify(kafkaTemplate, never()).send(any(), any());
    }

    @Test
    void processTransfer_ShouldUpdateBothCards(){
        Long fromUserId = 1L;
        Long toUserId = 2L;
        BigDecimal amount = new BigDecimal("50.00");

        CardDTO toCardDto = new CardDTO();
        toCardDto.setId(2L);
        toCardDto.setBalance(new BigDecimal("200.00"));
        toCardDto.setCardNumber("6543210987654321");

        when(cardService.getCardByUserId(fromUserId)).thenReturn(cardDTO);
        when(cardService.getCardByUserId(toUserId)).thenReturn(toCardDto);

        cardTransactionService.processTransfer(fromUserId, toUserId, amount);

        verify(cardService, times(2)).updateCardById(any(), any());
        verify(kafkaTemplate, times(2)).send(eq("card-notifications"), any(
                CardNotificationEvent.class
        ));
    }
    @Test
    void processTransfer_WhenReceivingFails_ShouldRollbackPayment() {
        Long fromUserId = 1L;
        Long toUserId = 2L;
        BigDecimal amountToTransfer = new BigDecimal("150.00");

        when(cardService.getCardByUserId(fromUserId)).thenReturn(cardDTO);

        assertThrows(BaseTransactionException.class, ()->{
            cardTransactionService.processTransfer(fromUserId, toUserId, amountToTransfer);
        });
        verify(cardService, never()).updateCardById(any(), any());
        verify(kafkaTemplate, never()).send(any(), any());
    }
}
