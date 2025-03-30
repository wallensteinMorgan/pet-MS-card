package com.example.demo.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name = "cards")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CardEntity extends BaseEntity{

    @Column(nullable = false, unique = true)
    private Long accountId; //1-1

    @Column(nullable = false, unique = true)
    private Long userId; //1-1

    @Column(nullable = false)
    private String cardNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentSystem paymentSystem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardType cardType;

    @Column(columnDefinition = "TINYINT(1)")
    private Boolean active;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

}