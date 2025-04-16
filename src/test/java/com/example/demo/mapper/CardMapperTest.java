package com.example.demo.mapper;

import com.example.demo.dto.CardDTO;
import com.example.demo.entity.CardEntity;
import com.example.demo.entity.CardType;
import com.example.demo.entity.PaymentSystem;
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

import static org.assertj.core.api.Assertions.assertThat;


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
        Mockito.lenient().when(cardTypeMapper.toCardType("CREDIT")).thenReturn(CardType.CREDIT);
        Mockito.lenient().when(paymentSystemMapper.toPaymentSystem("VISA")).thenReturn(PaymentSystem.VISA);
        Mockito.lenient().when(dateMapper.toDate("2026-12-31")).thenReturn(LocalDate.of(2026, 12, 31));

        Mockito.lenient().when(cardTypeMapper.toString(CardType.CREDIT)).thenReturn("CREDIT");
        Mockito.lenient().when(paymentSystemMapper.toString(PaymentSystem.VISA)).thenReturn("VISA");

        defaultCardDto = CardDTO.builder()
                .accountId(1L)
                .userId(1L)
                .cardNumber("1234123412341234")
                .balance(new BigDecimal("1000.0"))
                .expiryDate("2026-12-31")
                .active(false)
                .cardType("CREDIT")
                .paymentSystem("VISA")
                .build();

        defaultCardEntity = CardEntity.builder()
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

    @Test
    public void toEntity_WithValidDTO_ReturnsCorrectEntity() {
        CardEntity result = cardMapper.toEntity(defaultCardDto);

        assertThat(result.getAccountId()).isEqualTo(defaultCardEntity.getAccountId());
        assertThat(result.getUserId()).isEqualTo(defaultCardEntity.getUserId());
        assertThat(result.getCardNumber()).isEqualTo(defaultCardEntity.getCardNumber());
        assertThat(result.getBalance()).isEqualTo(defaultCardEntity.getBalance());
        assertThat(result.getExpiryDate()).isEqualTo(defaultCardEntity.getExpiryDate());
        assertThat(result.getActive()).isEqualTo(defaultCardEntity.getActive());
        assertThat(result.getCardType()).isEqualTo(defaultCardEntity.getCardType());
        assertThat(result.getPaymentSystem()).isEqualTo(defaultCardEntity.getPaymentSystem());
    }

    @Test
    public void toEntity_WithNull_ThenReturnsNull() {
        assertThat(cardMapper.toEntity(null)).isNull();
    }

    @Test
    public void toEntity_WithSpacesInCardNumber_ReturnsCorrectEntity() {
        defaultCardDto.setCardNumber("1234 1234 1234 1234");
        CardEntity result = cardMapper.toEntity(defaultCardDto);
        assertThat(result.getCardNumber()).isEqualTo("************1234");
    }

    @Test
    public void toEntity_WithDashInCardNumber_ReturnsCorrectEntity() {
        defaultCardDto.setCardNumber("1234-1234-1234-1234");
        CardEntity result = cardMapper.toEntity(defaultCardDto);
        assertThat(result.getCardNumber()).isEqualTo("************1234");
    }

    @Test
    public void toDto_WithValidEntity_ThenReturnsCorrectDto() {
        CardDTO result = cardMapper.toDTO(defaultCardEntity);

        assertThat(result.getAccountId()).isEqualTo(defaultCardDto.getAccountId());
        assertThat(result.getUserId()).isEqualTo(defaultCardDto.getUserId());
        assertThat(result.getCardNumber()).isEqualTo("************1234");
        assertThat(result.getBalance()).isEqualTo(defaultCardDto.getBalance());
        assertThat(result.getExpiryDate()).isEqualTo(defaultCardDto.getExpiryDate());
        assertThat(result.getActive()).isEqualTo(defaultCardDto.getActive());
        assertThat(result.getCardType()).isEqualTo(defaultCardDto.getCardType());
        assertThat(result.getPaymentSystem()).isEqualTo(defaultCardDto.getPaymentSystem());
    }

    @Test
    public void toDto_WithNull_ThenReturnsNull() {
        assertThat(cardMapper.toDTO(null)).isNull();
    }

    @Test
    public void toEntity_ShouldMaskCardNumberCorrectly() {
        defaultCardDto.setCardNumber("5555555555555555");
        CardEntity result = cardMapper.toEntity(defaultCardDto);
        assertThat(result.getCardNumber()).isEqualTo("************5555");
    }

    @Test
    public void toDto_ShouldNotModifyCardNumber() {
        defaultCardEntity.setCardNumber("************9999");
        CardDTO result = cardMapper.toDTO(defaultCardEntity);
        assertThat(result.getCardNumber()).isEqualTo("************9999");
    }
}