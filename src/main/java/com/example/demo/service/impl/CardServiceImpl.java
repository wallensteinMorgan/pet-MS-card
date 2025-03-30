package com.example.demo.service.impl;


import com.example.demo.dto.CardDTO;
import com.example.demo.entity.CardEntity;
import com.example.demo.exception.CardNotFoundException;
import com.example.demo.exception.InvalidCardTypeException;
import com.example.demo.exception.InvalidPaymentSystemException;
import com.example.demo.mapper.CardMapper;
import com.example.demo.repository.CardRepository;
import com.example.demo.service.CardService;
import com.example.demo.util.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    @Transactional(readOnly = true)
    public List<CardDTO> getAllCards() {
        log.info("Getting all the cards");
        List<CardDTO> cards = cardRepository.findAll()
                .stream()
                .map(cardMapper::toDTO)
                .toList();
        log.info("Received {} cards", cards.size());
        return cards;
    }

    @Transactional(readOnly = true)
    @Override
    public CardDTO getCardById(Long id) {
        log.info("Getting a card with an id {}", id);
        CardDTO cardDTO = cardMapper.toDTO(getEntityById(id));
        log.info("Card with id {} successfully received", id);
        return cardDTO;
    }

    @Transactional(readOnly = true)
    @Override
    public CardDTO getCardByUserId(Long userId) {
        log.info("Getting a card by userId {}", userId);
        CardDTO cardDTO = cardMapper.toDTO(getEntityByUserId(userId));
        log.info("The card with the userId {} was successfully received", userId);
        return cardDTO;
    }

    @Transactional(readOnly = true)
    @Override
    public CardDTO getCardByAccountId(Long accountId) {
        log.info("Getting a card by AccountId {}", accountId);
        CardDTO cardDTO = cardMapper.toDTO(getEntityByAccountId(accountId));
        log.info("The card with the AccountId {} was successfully received", accountId);
        return cardDTO;
    }

    @Transactional
    @Override
    public CardDTO saveCardById(CardDTO cardDTO) {

        String convertToUpperCasePaymentSystem = cardDTO.getPaymentSystem().toUpperCase();
        String convertToUpperCaseCardType = cardDTO.getCardType().toUpperCase();

        cardDTO.setPaymentSystem(convertToUpperCasePaymentSystem);
        cardDTO.setCardType(convertToUpperCaseCardType);

        if (!Validator.isValidCardType(cardDTO.getCardType())) {
            log.error("Invalid card type: {}", cardDTO.getCardType());
            throw new InvalidCardTypeException("The card type is incorrect.");
        }
        if (!Validator.isValidPaymentSystem(cardDTO.getPaymentSystem())) {
            log.error("Incorrect payment system: {}", cardDTO.getPaymentSystem());
            throw new InvalidPaymentSystemException("The payment system is specified incorrectly.");
        }
        CardEntity cardEntity = cardMapper.toEntity(cardDTO);
        CardEntity savedCardEntity = cardRepository.save(cardEntity);
        log.info("The card was saved successfully: {}", savedCardEntity.getId());
        return cardMapper.toDTO(savedCardEntity);
    }

    @Transactional
    @Override
    public boolean deleteCardById(Long id) {
        log.info("Attempt to delete a card with an id {}", id);
        CardEntity entity = getEntityById(id);
        cardRepository.delete(entity);
        log.info("Card with id {} successfully deleted", id);
        return true;
    }

    @Transactional
    @Override
    public CardDTO updateCardById(Long id, Consumer<CardEntity> updateFunction) {
        log.info("Attempt to update a card with an id {}", id);
        if (!Validator.isValidId(id)) {
            log.error("Attempt to find a card with an incorrect id: {}", id);
            throw new IllegalArgumentException("the card id cannot be null or < 0");
        }
        CardEntity entity = getEntityById(id);
        updateFunction.accept(entity);
        cardRepository.save(entity);
        log.info("The card with the id {} has been successfully updated", id);
        return cardMapper.toDTO(entity);
    }

    private CardEntity getEntityById(Long id) {
        log.info("Attempt to get a card with an id {}", id);
        if (!Validator.isValidId(id)) {
            log.error("Attempt to find a card with an incorrect id: {}", id);
            throw new IllegalArgumentException("the card id cannot be null or < 0");
        }
        return cardRepository.findById(id).orElseThrow(() -> {
            log.error("Card with id {} not found", id);
            return new CardNotFoundException("A card with an id " + id + " not found");
        });
    }

    private CardEntity getEntityByUserId(Long id) {
        log.info("Attempt to get a card with user Id {}", id);
        if (!Validator.isValidId(id)) {
            log.error("Attempt to find a card with an invalid userId: {}", id);
            throw new IllegalArgumentException("the user Id of the card cannot be null or < 0");
        }
        return cardRepository.findCardEntityByUserId(id).orElseThrow(() -> {
            log.error("Card with user Id {} not found", id);
            return new CardNotFoundException("A card with a userId " + id + " not found");
        });
    }

    private CardEntity getEntityByAccountId(Long id) {
        log.info("Attempt to get a card with an AccountId {}", id);
        if (!Validator.isValidId(id)) {
            log.error("Attempt to find a card with an invalid AccountId: {}", id);
            throw new IllegalArgumentException("accountId card cannot be equal to null or < 0");
        }
        return cardRepository.findCardEntityByAccountId(id).orElseThrow(() -> {
            log.error("Card with a accountId {} not found", id);
            return new CardNotFoundException("card with a accountId " + id + " not found");
        });
    }
}