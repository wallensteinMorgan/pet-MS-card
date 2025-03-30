package com.example.demo.dto;


import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardDTO {

    //In CardDto id can be null
    private Long id;

    @NotNull(message = "account id is required")
    private Long accountId;

    @NotNull(message = "user id is required")
    private Long userId;

    @NotNull
    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
    private String cardNumber;

    @NotNull
    @DecimalMin(value = "0.00", message = "Balance cannot be negative")
    private BigDecimal balance;

    @NotNull(message = "Expiry date is required")
    private String expiryDate;

    private Boolean active;

    @NotBlank
    private String cardType;

    @NotBlank
    private String paymentSystem;

}