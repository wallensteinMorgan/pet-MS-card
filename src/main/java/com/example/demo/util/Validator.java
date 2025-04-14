package com.example.demo.util;

import com.example.demo.entity.CardType;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class Validator {

    public static boolean isValidId(Long id) {
        if (id == null) return false;
        return id >= 1;
    }

    public static boolean isValidAmount(BigDecimal amount) {
        if (amount == null) return false;
        return amount.compareTo(BigDecimal.valueOf(0)) > 0;
    }

    public static boolean isValidCardType(String cardType) {
        try {
            CardType.valueOf(cardType.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    public static boolean isValidPaymentSystem(String paymentSystem) {
        List<String> validPaymentSystems = Arrays.asList("VISA", "MASTERCARD");
        return paymentSystem != null && validPaymentSystems.contains(paymentSystem.toUpperCase());
    }

    public static boolean isValidDateString(String date) {
        if (date.length() != 10) return false;
        else return true;
    }
    public static boolean isExpiryDateValid(LocalDate expiryDate) {
        return expiryDate != null && expiryDate.isAfter(LocalDate.now());
    }
}
