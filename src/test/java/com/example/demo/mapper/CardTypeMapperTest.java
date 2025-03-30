package com.example.demo.mapper;

import com.example.demo.entity.CardType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CardTypeMapperTest {
    @Autowired
    private CardTypeMapper typeMapper;

    @Test
    public void toCardType_WithValidType_Successful() {
        String type = "CREDIT";
        assertEquals(CardType.CREDIT, typeMapper.toCardType(type));
    }

    @Test
    public void toCardType_WithNull_ReturnsNull() {
        assertNull(typeMapper.toCardType(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"UNKNOWN", "credit"})
    public void toCardType_WithInvalidType_ThrowsIllegalArgumentException(String type) {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> typeMapper.toCardType(type)
        );
        assertEquals("Unexpected enum constant: " + type, exception.getMessage());
    }

    @Test
    public void toString_WithValidType_Successful() {
        assertEquals("CREDIT", typeMapper.toString(CardType.CREDIT));
    }

    @Test
    public void toString_WithNull_ReturnsNull() {
        assertNull(typeMapper.toString(null));
    }
}
