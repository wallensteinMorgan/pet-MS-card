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
public class CardOperationInKafkaDTO {
    private Long id;
    @NotNull
    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
    private String cardNumber;
    @NotBlank
    private String cardType;
    @NotNull
    @DecimalMin(value = "0.00", message = "Balance cannot be negative")
    private BigDecimal balance;
    @NotNull
    @DecimalMin(value = "0.00", message = "Amount cannot be negative")
    private BigDecimal transferAmount;
    @NotNull(message = "Operation type is required")
    private String operationType;

    private LocalDateTime timestamp;
}