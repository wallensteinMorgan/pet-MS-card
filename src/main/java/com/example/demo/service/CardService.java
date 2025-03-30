package com.example.demo.service;

import com.example.demo.dto.CardDTO;
import com.example.demo.entity.CardEntity;
import java.util.List;
import java.util.function.Consumer;

public interface CardService {

    List<CardDTO> getAllCards();

    CardDTO getCardById(Long id);

    CardDTO getCardByUserId(Long userId);

    CardDTO getCardByAccountId(Long accountId);

    CardDTO saveCardById(CardDTO cardDTO);
    boolean deleteCardById(Long id);
    CardDTO updateCardById(Long id, Consumer<CardEntity> updateFunction);
}

