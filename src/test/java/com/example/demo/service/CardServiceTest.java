package com.example.demo.service;
import java.util.concurrent.TimeUnit;
import com.example.demo.dto.CardDTO;
import com.example.demo.entity.CardEntity;
import com.example.demo.entity.CardType;
import com.example.demo.entity.PaymentSystem;
import com.example.demo.exception.BaseValidationException;
import com.example.demo.exception.CardNotFoundException;
import com.example.demo.exception.InvalidBaseTypeException;
import com.example.demo.exception.InvalidPaymentSystemException;
import com.example.demo.mapper.CardMapper;
import com.example.demo.repository.CardRepository;
import com.example.demo.service.impl.CardServiceImpl;
import com.example.demo.service.impl.RedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {
    @Mock
    private CardRepository cardRepository;
    @Mock
    private RedisService redisService;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardServiceImpl cardService;

    private CardEntity cardEntity;
    private CardDTO cardDTO;

    @BeforeEach
    void setUp() {
        cardEntity = new CardEntity();
        cardEntity.setId(1L);
        cardEntity.setAccountId(100L);
        cardEntity.setUserId(200L);
        cardEntity.setCardNumber("1234567812345678");
        cardEntity.setBalance(new BigDecimal("100.00"));
        cardEntity.setExpiryDate(LocalDate.of(2025, 12, 1));
        cardEntity.setActive(true);
        cardEntity.setCardType(CardType.CREDIT);
        cardEntity.setPaymentSystem(PaymentSystem.VISA);

        cardDTO = new CardDTO();
        cardDTO.setId(1L);
        cardDTO.setAccountId(100L);
        cardDTO.setUserId(200L);
        cardDTO.setCardNumber("1234567812345678");
        cardDTO.setBalance(new BigDecimal("100.00"));
        cardDTO.setExpiryDate("12/25");
        cardDTO.setActive(true);
        cardDTO.setCardType("CREDIT");
        cardDTO.setPaymentSystem("VISA");
    }

    @Test
    void testGetAllCards_withCards() {
        List<CardEntity> entities = Arrays.asList(cardEntity);
        when(cardRepository.findAll()).thenReturn(entities);
        when(cardMapper.toDTO(cardEntity)).thenReturn(cardDTO);
        when(redisService.get("cards:all")).thenReturn(null);

        List<CardDTO> result = cardService.getAllCards();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(cardDTO, result.get(0));
        verify(cardRepository, times(1)).findAll();
        verify(cardMapper, times(1)).toDTO(cardEntity);
        verify(redisService, times(1)).set(eq("cards:all"), anyList(), eq(30L), eq(TimeUnit.MINUTES));
    }

    @Test
    void testGetAllCards_empty() {
        when(cardRepository.findAll()).thenReturn(Collections.emptyList());
        when(redisService.get("cards:all")).thenReturn(null);

        List<CardDTO> result = cardService.getAllCards();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(cardRepository, times(1)).findAll();
        verify(redisService, times(1)).set(eq("cards:all"), anyList(), eq(30L), eq(TimeUnit.MINUTES));
    }

    @Test
    void testGetCardById_success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(cardEntity));
        when(cardMapper.toDTO(cardEntity)).thenReturn(cardDTO);
        when(redisService.get("card:id:1")).thenReturn(null);

        CardDTO result = cardService.getCardById(1L);

        assertNotNull(result);
        assertEquals(cardDTO, result);
        verify(cardRepository, times(1)).findById(1L);
        verify(cardMapper, times(1)).toDTO(cardEntity);
        verify(redisService, times(1)).set(eq("card:id:1"), eq(cardDTO), eq(1L), eq(TimeUnit.HOURS));
    }

    @Test
    void testGetCardById_invalidId() {
        lenient().when(redisService.get(anyString())).thenReturn(null);

        assertThrows(BaseValidationException.class, () -> cardService.getCardById(-1L));

        verify(cardRepository, never()).findById(any());
    }

    @Test
    void testGetCardById_notFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());
        when(redisService.get("card:id:1")).thenReturn(null);

        assertThrows(CardNotFoundException.class, () -> cardService.getCardById(1L));
        verify(cardRepository, times(1)).findById(1L);
        verify(redisService, never()).set(any(), any(), anyLong(), any());
    }

    @Test
    void testGetCardByUserId_success() {
        when(cardRepository.findCardEntityByUserId(200L)).thenReturn(Optional.of(cardEntity));
        when(cardMapper.toDTO(cardEntity)).thenReturn(cardDTO);
        when(redisService.get("card:user:200")).thenReturn(null);

        CardDTO result = cardService.getCardByUserId(200L);

        assertNotNull(result);
        assertEquals(cardDTO, result);
        verify(cardRepository, times(1)).findCardEntityByUserId(200L);
        verify(cardMapper, times(1)).toDTO(cardEntity);
        verify(redisService, times(1)).set(eq("card:user:200"), eq(cardDTO), eq(1L), eq(TimeUnit.HOURS));
    }

    @Test
    void testGetCardByUserId_invalidId() {
        lenient().when(redisService.get(anyString())).thenReturn(null);

        assertThrows(BaseValidationException.class, () -> cardService.getCardByUserId(-1L));

        verify(cardRepository, never()).findCardEntityByUserId(any());
    }

    @Test
    void testGetCardByUserId_notFound() {
        when(cardRepository.findCardEntityByUserId(200L)).thenReturn(Optional.empty());
        when(redisService.get("card:user:200")).thenReturn(null);

        assertThrows(CardNotFoundException.class, () -> cardService.getCardByUserId(200L));
        verify(cardRepository, times(1)).findCardEntityByUserId(200L);
        verify(redisService, never()).set(any(), any(), anyLong(), any());
    }

    @Test
    void testGetCardByAccountId_success() {
        when(cardRepository.findCardEntityByAccountId(100L)).thenReturn(Optional.of(cardEntity));
        when(cardMapper.toDTO(cardEntity)).thenReturn(cardDTO);
        when(redisService.get("card:account:100")).thenReturn(null);

        CardDTO result = cardService.getCardByAccountId(100L);

        assertNotNull(result);
        assertEquals(cardDTO, result);
        verify(cardRepository, times(1)).findCardEntityByAccountId(100L);
        verify(cardMapper, times(1)).toDTO(cardEntity);
        verify(redisService, times(1)).set(eq("card:account:100"), eq(cardDTO), eq(1L), eq(TimeUnit.HOURS));
    }

    @Test
    void testGetCardByAccountId_invalidId() {
        lenient().when(redisService.get(anyString())).thenReturn(null);

        assertThrows(BaseValidationException.class, () -> cardService.getCardByAccountId(-1L));

        verify(cardRepository, never()).findCardEntityByAccountId(any());
    }

    @Test
    void testGetCardByAccountId_notFound() {
        when(cardRepository.findCardEntityByAccountId(100L)).thenReturn(Optional.empty());
        when(redisService.get("card:account:100")).thenReturn(null);

        assertThrows(CardNotFoundException.class, () -> cardService.getCardByAccountId(100L));
        verify(cardRepository, times(1)).findCardEntityByAccountId(100L);
        verify(redisService, never()).set(any(), any(), anyLong(), any());
    }

    @Test
    void testSaveCardById_success() {
        CardDTO inputDTO = new CardDTO();
        inputDTO.setAccountId(100L);
        inputDTO.setUserId(200L);
        inputDTO.setCardNumber("1234567812345678");
        inputDTO.setBalance(new BigDecimal("50.00"));
        inputDTO.setExpiryDate("11/25");
        inputDTO.setActive(true);
        inputDTO.setCardType("credit");
        inputDTO.setPaymentSystem("visa");

        when(cardMapper.toEntity(any(CardDTO.class))).thenReturn(cardEntity);
        when(cardRepository.save(cardEntity)).thenReturn(cardEntity);
        when(cardMapper.toDTO(cardEntity)).thenReturn(cardDTO);

        CardDTO result = cardService.saveCardById(inputDTO);

        assertNotNull(result);
        assertEquals("VISA", result.getPaymentSystem());
        assertEquals("CREDIT", result.getCardType());
        verify(cardMapper, times(1)).toEntity(any(CardDTO.class));
        verify(cardRepository, times(1)).save(cardEntity);
        verify(cardMapper, times(1)).toDTO(cardEntity);
        verify(redisService, times(1)).delete("card:id:1");
        verify(redisService, times(1)).delete("card:user:200");
        verify(redisService, times(1)).delete("card:account:100");
        verify(redisService, times(1)).delete("cards:all");
    }

    @Test
    void testSaveCardById_invalidCardType() {
        CardDTO inputDTO = new CardDTO();
        inputDTO.setAccountId(100L);
        inputDTO.setUserId(200L);
        inputDTO.setCardType("invalid");
        inputDTO.setPaymentSystem("VISA");

        assertThrows(InvalidBaseTypeException.class, () -> cardService.saveCardById(inputDTO));
        verifyNoInteractions(redisService);
    }

    @Test
    void testSaveCardById_invalidPaymentSystem() {
        CardDTO inputDTO = new CardDTO();
        inputDTO.setAccountId(100L);
        inputDTO.setUserId(200L);
        inputDTO.setCardType("CREDIT");
        inputDTO.setPaymentSystem("invalid");

        assertThrows(InvalidPaymentSystemException.class, () -> cardService.saveCardById(inputDTO));
        verifyNoInteractions(redisService);
    }
    @Test
    void testSaveCardById_invalidAccountId() {
        CardDTO inputDTO = new CardDTO();
        inputDTO.setAccountId(-100L);
        inputDTO.setUserId(200L);
        inputDTO.setCardType("CREDIT");
        inputDTO.setPaymentSystem("VISA");

        assertThrows(BaseValidationException.class, () -> cardService.saveCardById(inputDTO));
        verifyNoInteractions(redisService);
    }

    @Test
    void testDeleteCardById_success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(cardEntity));

        boolean result = cardService.deleteCardById(1L);

        assertTrue(result);
        verify(cardRepository, times(1)).delete(cardEntity);
        verify(redisService, times(1)).delete("card:id:1");
        verify(redisService, times(1)).delete("card:user:200");
        verify(redisService, times(1)).delete("card:account:100");
        verify(redisService, times(1)).delete("cards:all");
    }

    @Test
    void testDeleteCardById_invalidId() {
        assertThrows(BaseValidationException.class, () -> cardService.deleteCardById(-1L));
        verifyNoInteractions(redisService);
        verifyNoInteractions(cardRepository);
    }

    @Test
    void testDeleteCardById_notFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardService.deleteCardById(1L));
        verify(cardRepository, times(1)).findById(1L);
        verifyNoInteractions(redisService);
    }

    @Test
    void testUpdateCardById_success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(cardEntity));
        when(cardRepository.save(cardEntity)).thenReturn(cardEntity);
        when(cardMapper.toDTO(cardEntity)).thenReturn(cardDTO);

        Consumer<CardEntity> updateFunction = entity -> entity.setActive(false);
        CardDTO result = cardService.updateCardById(1L, updateFunction);

        assertNotNull(result);
        assertFalse(cardEntity.getActive());
        verify(cardRepository, times(1)).findById(1L);
        verify(cardRepository, times(1)).save(cardEntity);
        verify(cardMapper, times(1)).toDTO(cardEntity);
        verify(redisService, times(1)).delete("card:id:1");
        verify(redisService, times(1)).delete("card:user:200");
        verify(redisService, times(1)).delete("card:account:100");
        verify(redisService, times(1)).delete("cards:all");
    }

    @Test
    void testUpdateCardById_invalidId() {
        Consumer<CardEntity> updateFunction = entity -> {};
        assertThrows(BaseValidationException.class, () -> cardService.updateCardById(-1L, updateFunction));
        verifyNoInteractions(redisService);
    }

    @Test
    void testUpdateCardById_notFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());
        Consumer<CardEntity> updateFunction = entity -> {};

        assertThrows(CardNotFoundException.class, () -> cardService.updateCardById(1L, updateFunction));
        verify(cardRepository, times(1)).findById(1L);
        verifyNoInteractions(redisService);
    }
}
