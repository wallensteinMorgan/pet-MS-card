package com.example.demo.mapper;

import com.example.demo.entity.PaymentSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class PaymentSystemMapperTest {
    @Autowired
    private PaymentSystemMapper paymentMapper;

    @Test
    public void toCardType_WithValidType_Successful() {
        String type = "VISA";
        assertEquals(PaymentSystem.VISA, paymentMapper.toPaymentSystem(type));
    }

    @Test
    public void toCardType_WithNull_ReturnsNull() {
        assertNull(paymentMapper.toPaymentSystem(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"UNKNOWN", "visa"})
    public void toCardType_WithInvalidType_ThrowsIllegalArgumentException(String type) {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> paymentMapper.toPaymentSystem(type)
        );
        assertEquals("Unexpected enum constant: " + type, exception.getMessage());
    }

    @Test
    public void toString_WithValidType_Successful() {
        assertEquals("VISA", paymentMapper.toString(PaymentSystem.VISA));
    }

    @Test
    public void toString_WithNull_ReturnsNull() {
        assertNull(paymentMapper.toString(null));
    }
}
