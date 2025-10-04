package com.example.demo.service.impl;

import com.example.demo.dto.CardDTO;
import com.example.demo.dto.CardNotificationEvent;
import com.example.demo.exception.BaseTransactionException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.service.CardService;
import com.example.demo.service.CardTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardTransactionServiceImpl implements CardTransactionService {

    private final CardService cardService;
    private final KafkaTemplate<String, CardNotificationEvent> kafkaTemplate;
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void processReceiving(Long userId, BigDecimal amount) {
        CardDTO card =  cardService.getCardByUserId(userId);

        BigDecimal newBalance = card.getBalance().subtract(amount);
        cardService.updateCardById(card.getId(),
                entity -> entity.setBalance(entity.getBalance().add(amount)));

        CardNotificationEvent event = CardNotificationEvent.builder()
                .email("valentinakhvatova7@gmail.com")
                .cardNumber(card.getCardNumber())
                .amount(amount)
                .operationType("DEPOSIT")
                .currentBalance(newBalance)
                .timestamp(LocalDateTime.now())
                .build();
        kafkaTemplate.send("card-notifications", event);
        log.info("Sent deposit notification to Kafka: {}", event);
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void processPayment(Long userId, BigDecimal amount) {
        CardDTO card = cardService.getCardByUserId(userId);
        if (card.getBalance().compareTo(amount) < 0) {
            throw new BaseTransactionException(ErrorCode.INSUFFICIENT_BALANCE,
                    card.getCardNumber(), card.getBalance(), amount);
        }
        BigDecimal newBalance = card.getBalance().subtract(amount);
        cardService.updateCardById(card.getId(),
                entity -> entity.setBalance(entity.getBalance().subtract(amount)));

        CardNotificationEvent event = CardNotificationEvent.builder()
                .email("valentinakhvatova7@gmail.com")
                .cardNumber(card.getCardNumber())
                .amount(amount)
                .operationType("PAYMENT")
                .currentBalance(newBalance)
                .timestamp(LocalDateTime.now())
                .build();
        kafkaTemplate.send("card-notifications", event);
        log.info("Sent payment notification to Kafka: {}", event);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void processTransfer(Long fromUserId, Long toUserId, BigDecimal amount) {
        // 1. Получаем данные карт
        processPayment(fromUserId, amount);  // Списание у отправителя
        processReceiving(toUserId, amount);  // Зачисление получателю

        // 3. Логируем успешный перевод
        log.info("Transfer completed: {} from user {} to user {}", amount, fromUserId, toUserId);
    }
}