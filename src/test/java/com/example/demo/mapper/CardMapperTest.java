package com.example.demo.mapper;

import com.example.demo.dto.CardDTO;
import com.example.demo.entity.CardEntity;
import com.example.demo.entity.CardType;
import com.example.demo.entity.PaymentSystem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class CardMapperTest {

    @Mock
    private CardTypeMapper cardTypeMapper;

    @Mock
    private PaymentSystemMapper paymentSystemMapper;

    @Mock
    private DateMapper dateMapper;

    @InjectMocks
    private CardMapperImpl cardMapper;

    private CardDTO defaultCardDto;

    private CardEntity defaultCardEntity;

    @BeforeEach
    public void init() {
        defaultCardEntity = createDefaultCardEntity();
        defaultCardDto = createDefaultCardDto();
        Mockito.lenient().when(cardTypeMapper.toCardType("CREDIT")).thenReturn(CardType.CREDIT);
        Mockito.lenient().when(paymentSystemMapper.toPaymentSystem("VISA")).thenReturn(PaymentSystem.VISA);
        Mockito.lenient().when(dateMapper.toDate("2026-12-31")).thenReturn(LocalDate.of(2026, 12,31));

        Mockito.lenient().when(cardTypeMapper.toString(CardType.CREDIT)).thenReturn("CREDIT");
        Mockito.lenient().when(paymentSystemMapper.toString(PaymentSystem.VISA)).thenReturn("VISA");
    }

    @Test
    public void toEntity_WithValidDTO_ReturnsCorrectEntity() {
        Assertions.assertEquals(defaultCardEntity, cardMapper.toEntity(defaultCardDto));
    }

    @Test
    public void toEntity_WithNull_ThenReturnsNull() {
        Assertions.assertNull(cardMapper.toEntity(null));
    }

    @Test
    public void toEntity_WithSpacesInCardNumber_ReturnsCorrectEntity() {
        defaultCardDto.setCardNumber("1234 1234 1234 1234");
        Assertions.assertEquals(defaultCardEntity, cardMapper.toEntity(defaultCardDto));
    }

    @Test
    public void toEntity_WithDashInCardNumber_ReturnsCorrectEntity() {
        defaultCardDto.setCardNumber("1234-1234-1234-1234");
        Assertions.assertEquals(defaultCardEntity, cardMapper.toEntity(defaultCardDto));
    }

    @Test
    public void toDto_WithValidEntity_ThenReturnsCorrectDto() {
        defaultCardDto.setCardNumber("************1234");
        Assertions.assertEquals(defaultCardDto, cardMapper.toDTO(defaultCardEntity));
    }

    @Test
    public void toDto_WithNull_ThenReturnsNull() {
        Assertions.assertNull(cardMapper.toDTO(null));
    }

    private CardDTO createDefaultCardDto() {
        return CardDTO.builder()
                .accountId(1L)
                .userId(1L)
                .cardNumber("1234123412341234")
                .balance(new BigDecimal("1000.0"))
                .expiryDate("2026-12-31")
                .active(false)
                .cardType("CREDIT")
                .paymentSystem("VISA")
                .build();
    }

    private CardEntity createDefaultCardEntity() {
        return CardEntity.builder()
                .accountId(1L)
                .userId(1L)
                .cardNumber("************1234")
                .balance(new BigDecimal("1000.0"))
                .expiryDate(LocalDate.of(2026, 12, 31))
                .active(false)
                .cardType(CardType.CREDIT)
                .paymentSystem(PaymentSystem.VISA)
                .build();
    }

}