package com.example.demo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardNotificationEvent {
    @NotBlank
    private String email;
    @NotNull
    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
    private String cardNumber;
    @NotNull
    @DecimalMin(value = "0.00", message = "Balance cannot be negative")
    private BigDecimal amount;
    @NotBlank
    private String operationType;
    private BigDecimal currentBalance;
    private LocalDateTime timestamp;
}
