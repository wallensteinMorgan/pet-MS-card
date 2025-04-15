package com.example.demo.controller;

import com.example.demo.service.CardTransactionService;
import com.example.demo.util.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;

@RestController
@RequestMapping("/transfer")
@Slf4j
@RequiredArgsConstructor
public class TransferController {

    private final CardTransactionService transactionService;

    @PostMapping("/pay")
    public void processPayment(@RequestParam Long userId, @RequestParam BigDecimal amount) {
        checkAmount(amount);
        transactionService.processPayment(userId, amount);
    }

    @PostMapping("/receive")
    public void processReceiving(@RequestParam Long userId, @RequestParam BigDecimal amount) {
        checkAmount(amount);
        transactionService.processReceiving(userId, amount);
    }

    @PostMapping("/transfer")
    public void processTransfer(@RequestParam Long fromUserId, @RequestParam Long toUserId, @RequestParam BigDecimal amount) {

        checkAmount(amount);
        transactionService.processTransfer(fromUserId, toUserId, amount);
    }
    private void checkAmount(BigDecimal amount) {
        if (! Validator.isValidAmount(amount)) {
            log.error("amount of money is not valid, it should be != null and > 0");
            throw new IllegalArgumentException("Invalid amount of money");
        }
    }
}