package com.example.demo.service;

import java.math.BigDecimal;
public interface CardTransactionService {

    void processPayment(Long id, BigDecimal amount);

    void processReceiving(Long id, BigDecimal amount);

    void processTransfer(Long fromId, Long toId, BigDecimal amount);

}