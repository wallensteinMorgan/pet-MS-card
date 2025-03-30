package com.example.demo.service.impl;

import com.example.demo.service.CardService;
import com.example.demo.service.CardTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardTransactionServiceImpl implements CardTransactionService {

    private final CardService cardService;
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void processReceiving(Long id, BigDecimal amount) {
        cardService.updateCardById(id,
                (entity) -> entity.setBalance(entity.getBalance().add(amount))
        );
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void processPayment(Long id, BigDecimal amount) {
        cardService.updateCardById(id,
                (entity) -> {
                    if (entity.getBalance().compareTo(amount) < 0) {
                        log.error("On card {} not enough money for payment. on card: {}, needed: {}", id, entity.getBalance(), amount);
                        throw new RuntimeException("Not enough money on card " + id);
                    }
                    entity.setBalance(entity.getBalance().subtract(amount));
                }
        );
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void processTransfer(Long fromId, Long toId, BigDecimal amount) {
        processPayment(fromId, amount);
        processReceiving(toId, amount);
    }
}