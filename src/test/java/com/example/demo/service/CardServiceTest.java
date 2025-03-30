package com.example.demo.service;

import com.example.demo.dto.CardDTO;
import com.example.demo.entity.CardEntity;
import com.example.demo.exception.CardNotFoundException;
import com.example.demo.exception.InvalidCardTypeException;
import com.example.demo.exception.InvalidPaymentSystemException;
import com.example.demo.mapper.CardMapper;
import com.example.demo.repository.CardRepository;
import com.example.demo.service.impl.CardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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

        cardDTO = new CardDTO();
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

        List<CardDTO> result = cardService.getAllCards();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(cardDTO.getAccountId(), result.get(0).getAccountId());
        assertEquals(cardDTO.getUserId(), result.get(0).getUserId());
        assertEquals(cardDTO.getCardNumber(), result.get(0).getCardNumber());
        assertEquals(cardDTO.getPaymentSystem(), result.get(0).getPaymentSystem());
        assertEquals(cardDTO.getCardType(), result.get(0).getCardType());

        verify(cardRepository, times(1)).findAll();
        verify(cardMapper, times(1)).toDTO(cardEntity);
    }

    @Test
    void testGetAllCards_empty() {
        when(cardRepository.findAll()).thenReturn(Collections.emptyList());

        List<CardDTO> result = cardService.getAllCards();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(cardRepository, times(1)).findAll();
    }

    @Test
    void testGetCardById_success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(cardEntity));
        when(cardMapper.toDTO(cardEntity)).thenReturn(cardDTO);

        CardDTO result = cardService.getCardById(1L);

        assertNotNull(result);
        assertEquals(cardDTO.getAccountId(), result.getAccountId());
        assertEquals(cardDTO.getUserId(), result.getUserId());
        assertEquals(cardDTO.getCardNumber(), result.getCardNumber());
        assertEquals(cardDTO.getPaymentSystem(), result.getPaymentSystem());
        assertEquals(cardDTO.getCardType(), result.getCardType());
        verify(cardRepository, times(1)).findById(1L);
        verify(cardMapper, times(1)).toDTO(cardEntity);
    }

    @Test
    void testGetCardById_invalidId() {
        assertThrows(IllegalArgumentException.class, () -> cardService.getCardById(-1L));
    }

    @Test
    void testGetCardById_notFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardService.getCardById(1L));
        verify(cardRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCardByUserId_success() {
        when(cardRepository.findCardEntityByUserId(200L)).thenReturn(Optional.of(cardEntity));
        when(cardMapper.toDTO(cardEntity)).thenReturn(cardDTO);

        CardDTO result = cardService.getCardByUserId(200L);

        assertNotNull(result);
        assertEquals(cardDTO.getAccountId(), result.getAccountId());
        assertEquals(cardDTO.getUserId(), result.getUserId());
        assertEquals(cardDTO.getCardNumber(), result.getCardNumber());
        assertEquals(cardDTO.getPaymentSystem(), result.getPaymentSystem());
        assertEquals(cardDTO.getCardType(), result.getCardType());
        verify(cardRepository, times(1)).findCardEntityByUserId(200L);
        verify(cardMapper, times(1)).toDTO(cardEntity);
    }

    @Test
    void testGetCardByUserId_invalidId() {
        assertThrows(IllegalArgumentException.class, () -> cardService.getCardByUserId(-1L));
    }

    @Test
    void testGetCardByUserId_notFound() {
        when(cardRepository.findCardEntityByUserId(200L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardService.getCardByUserId(200L));
        verify(cardRepository, times(1)).findCardEntityByUserId(200L);
    }

    @Test
    void testGetCardByAccountId_success() {
        when(cardRepository.findCardEntityByAccountId(100L)).thenReturn(Optional.of(cardEntity));
        when(cardMapper.toDTO(cardEntity)).thenReturn(cardDTO);

        CardDTO result = cardService.getCardByAccountId(100L);

        assertNotNull(result);
        assertEquals(cardDTO.getAccountId(), result.getAccountId());
        assertEquals(cardDTO.getUserId(), result.getUserId());
        assertEquals(cardDTO.getCardNumber(), result.getCardNumber());
        assertEquals(cardDTO.getPaymentSystem(), result.getPaymentSystem());
        assertEquals(cardDTO.getCardType(), result.getCardType());
        verify(cardRepository, times(1)).findCardEntityByAccountId(100L);
        verify(cardMapper, times(1)).toDTO(cardEntity);
    }

    @Test
    void testGetCardByAccountId_invalidId() {
        assertThrows(IllegalArgumentException.class, () -> cardService.getCardByAccountId(-1L));
    }

    @Test
    void testGetCardByAccountId_notFound() {
        when(cardRepository.findCardEntityByAccountId(100L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardService.getCardByAccountId(100L));
        verify(cardRepository, times(1)).findCardEntityByAccountId(100L);
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

        CardEntity entityToSave = new CardEntity();
        CardEntity savedEntity = new CardEntity();
        savedEntity.setId(1L);

        CardDTO returnedDTO = new CardDTO();
        returnedDTO.setAccountId(100L);
        returnedDTO.setUserId(200L);
        returnedDTO.setCardNumber("1234567812345678");
        returnedDTO.setBalance(new BigDecimal("50.00"));
        returnedDTO.setExpiryDate("11/25");
        returnedDTO.setActive(true);
        returnedDTO.setCardType("CREDIT");
        returnedDTO.setPaymentSystem("VISA");

        when(cardMapper.toEntity(ArgumentMatchers.any(CardDTO.class))).thenReturn(entityToSave);
        when(cardRepository.save(entityToSave)).thenReturn(savedEntity);
        when(cardMapper.toDTO(savedEntity)).thenReturn(returnedDTO);

        CardDTO result = cardService.saveCardById(inputDTO);

        assertNotNull(result);
        assertEquals("VISA", result.getPaymentSystem());
        assertEquals("CREDIT", result.getCardType());
        assertEquals(100L, result.getAccountId());
        assertEquals(200L, result.getUserId());
        assertEquals("1234567812345678", result.getCardNumber());
        verify(cardMapper, times(1)).toEntity(ArgumentMatchers.any(CardDTO.class));
        verify(cardRepository, times(1)).save(entityToSave);
        verify(cardMapper, times(1)).toDTO(savedEntity);
    }

    @Test
    void testSaveCardById_invalidCardType() {
        CardDTO inputDTO = new CardDTO();
        inputDTO.setAccountId(100L);
        inputDTO.setUserId(200L);
        inputDTO.setCardNumber("1234567812345678");
        inputDTO.setBalance(new BigDecimal("50.00"));
        inputDTO.setExpiryDate("11/25");
        inputDTO.setActive(true);
        inputDTO.setCardType("invalid");
        inputDTO.setPaymentSystem("VISA");

        assertThrows(InvalidCardTypeException.class, () -> cardService.saveCardById(inputDTO));
    }

    @Test
    void testSaveCardById_invalidPaymentSystem() {
        CardDTO inputDTO = new CardDTO();
        inputDTO.setAccountId(100L);
        inputDTO.setUserId(200L);
        inputDTO.setCardNumber("1234567812345678");
        inputDTO.setBalance(new BigDecimal("50.00"));
        inputDTO.setExpiryDate("11/25");
        inputDTO.setActive(true);
        inputDTO.setCardType("CREDIT");
        inputDTO.setPaymentSystem("invalid");

        assertThrows(InvalidPaymentSystemException.class, () -> cardService.saveCardById(inputDTO));
    }

    @Test
    void testDeleteCardById_success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(cardEntity));

        boolean result = cardService.deleteCardById(1L);

        assertTrue(result);
        verify(cardRepository, times(1)).delete(cardEntity);
    }

    @Test
    void testDeleteCardById_invalidId() {
        assertThrows(IllegalArgumentException.class, () -> cardService.deleteCardById(-1L));
    }

    @Test
    void testDeleteCardById_notFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardService.deleteCardById(1L));
        verify(cardRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateCardById_success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(cardEntity));
        when(cardRepository.save(cardEntity)).thenReturn(cardEntity);
        when(cardMapper.toDTO(cardEntity)).thenReturn(cardDTO);

        Consumer<CardEntity> updateFunction = (entity) -> entity.setActive(false);
        CardDTO result = cardService.updateCardById(1L, updateFunction);

        assertNotNull(result);
        verify(cardRepository, times(1)).findById(1L);
        verify(cardRepository, times(1)).save(cardEntity);
        verify(cardMapper, times(1)).toDTO(cardEntity);
    }

    @Test
    void testUpdateCardById_invalidId() {
        Consumer<CardEntity> updateFunction = (entity) -> {
        };
        assertThrows(IllegalArgumentException.class, () -> cardService.updateCardById(-1L, updateFunction));
    }

    @Test
    void testUpdateCardById_notFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());
        Consumer<CardEntity> updateFunction = (entity) -> {
        };
        assertThrows(CardNotFoundException.class, () -> cardService.updateCardById(1L, updateFunction));
        verify(cardRepository, times(1)).findById(1L);
    }
}
