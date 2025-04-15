package com.example.demo.controller;

import com.example.demo.dto.CardDTO;
import com.example.demo.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/cards")
public class CardController {
    private final CardService cardService;

    @GetMapping
    public List<CardDTO> getAllCards() {
        return cardService.getAllCards();
    }

    @GetMapping("id/{id}")
    public CardDTO getCardById(@PathVariable Long id) {
        return cardService.getCardById(id);
    }

    @GetMapping("/user/{userId}")
    public CardDTO getCardByUserId(@PathVariable Long userId) {
        return cardService.getCardByUserId(userId);
    }

    @GetMapping("/account/{accountId}")
    public CardDTO getCardByAccountId(@PathVariable Long accountId) {
        return cardService.getCardByAccountId(accountId);
    }

    @PostMapping
    public CardDTO createCard(@RequestBody @Valid CardDTO cardDTO) {
        log.info("Received request: {}", cardDTO);
        return cardService.saveCardById(cardDTO);
    }

    @PostMapping("/setActive/{id}/{active}")
    public CardDTO setActive(
            @PathVariable Long id,
            @PathVariable Boolean active) {
        return cardService.updateCardById(id,
                (entity) -> entity.setActive(active)
        );
    }

    @DeleteMapping("/{id}")
    public boolean deleteById(@PathVariable Long id) {
        return cardService.deleteCardById(id);
    }
}