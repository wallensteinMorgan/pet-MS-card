package com.example.demo.service.impl;

import com.example.demo.dto.CardDTO;
import com.example.demo.entity.CardEntity;
import com.example.demo.exception.*;
import com.example.demo.mapper.CardMapper;
import com.example.demo.repository.CardRepository;
import com.example.demo.service.CardService;
import com.example.demo.util.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final RedisService redisService;
    private static final String CARD_BY_ID_PREFIX = "card:id:";
    private static final String CARD_BY_USER_PREFIX = "card:user:";
    private static final String CARD_BY_ACCOUNT_PREFIX = "card:account:";
    private static final String ALL_CARDS_KEY = "cards:all";
    private static final long CACHE_TTL_HOURS = 1;
    private static final long CACHE_TTL_MINUTES = 30;

    @Transactional(readOnly = true)
    public List<CardDTO> getAllCards() {
        log.info("Checking cache for all cards");
        List<CardDTO> cachedCards = (List<CardDTO>) redisService.get(ALL_CARDS_KEY);
        if (cachedCards != null) {
            log.info("Returning {} cards from cache", cachedCards.size());
            return cachedCards;
        }

        log.info("Cache miss - loading all cards from DB");
        List<CardDTO> cards = cardRepository.findAll()
                .stream()
                .map(cardMapper::toDTO)
                .toList();

        redisService.set(ALL_CARDS_KEY, cards, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        log.info("Cached {} cards for {} minutes", cards.size(), CACHE_TTL_MINUTES);
        return cards;
    }


    @Transactional(readOnly = true)
    @Override
    public CardDTO getCardById(Long id) {
        String cacheKey = CARD_BY_ID_PREFIX + id;
        log.info("Checking cache for card {}", id);

        CardDTO cachedCard = (CardDTO) redisService.get(cacheKey);
        if (cachedCard != null) {
            log.info("Returning cached card {}", id);
            return cachedCard;
        }

        log.info("Cache miss - loading card {} from DB", id);
        CardDTO cardDTO = cardMapper.toDTO(getEntityById(id));
        redisService.set(cacheKey, cardDTO, CACHE_TTL_HOURS, TimeUnit.HOURS);
        log.info("Cached card {} for {} hours", id, CACHE_TTL_HOURS);
        return cardDTO;
    }

    @Transactional(readOnly = true)
    @Override
    public CardDTO getCardByUserId(Long userId) {
        String cacheKey = CARD_BY_USER_PREFIX + userId;
        log.info("Checking cache for user's card {}", userId);

        CardDTO cachedCard = (CardDTO) redisService.get(cacheKey);
        if (cachedCard != null) {
            log.info("Returning cached card for user {}", userId);
            return cachedCard;
        }

        log.info("Cache miss - loading card for user {} from DB", userId);
        CardDTO cardDTO = cardMapper.toDTO(getEntityByUserId(userId));
        redisService.set(cacheKey, cardDTO, CACHE_TTL_HOURS, TimeUnit.HOURS);
        log.info("Cached card for user {} for {} hours", userId, CACHE_TTL_HOURS);
        return cardDTO;
    }

    @Transactional(readOnly = true)
    @Override
    public CardDTO getCardByAccountId(Long accountId) {
        String cacheKey = CARD_BY_ACCOUNT_PREFIX + accountId;
        log.info("Checking cache for account's card {}", accountId);

        CardDTO cachedCard = (CardDTO) redisService.get(cacheKey);
        if (cachedCard != null) {
            log.info("Returning cached card for account {}", accountId);
            return cachedCard;
        }

        log.info("Cache miss - loading card for account {} from DB", accountId);
        CardDTO cardDTO = cardMapper.toDTO(getEntityByAccountId(accountId));
        redisService.set(cacheKey, cardDTO, CACHE_TTL_HOURS, TimeUnit.HOURS);
        log.info("Cached card for account {} for {} hours", accountId, CACHE_TTL_HOURS);
        return cardDTO;
    }

    @Transactional
    @Override
    public CardDTO saveCardById(CardDTO cardDTO) {

        String convertToUpperCasePaymentSystem = cardDTO.getPaymentSystem().toUpperCase();
        String convertToUpperCaseCardType = cardDTO.getCardType().toUpperCase();

        cardDTO.setPaymentSystem(convertToUpperCasePaymentSystem);
        cardDTO.setCardType(convertToUpperCaseCardType);

        if (!Validator.isValidId(cardDTO.getAccountId()) || !Validator.isValidId(cardDTO.getUserId())) {
            throw new BaseValidationException("Incorrect userId or accountId, it should be > 0");
        }

        if (!Validator.isValidCardType(cardDTO.getCardType())) {
            throw new InvalidBaseTypeException("The card type is incorrect.");
        }
        if (!Validator.isValidPaymentSystem(cardDTO.getPaymentSystem())) {
            throw new InvalidPaymentSystemException("Incorrect payment system: " + cardDTO.getPaymentSystem());
        }
        CardEntity cardEntity = cardMapper.toEntity(cardDTO);
        CardEntity savedCardEntity = cardRepository.save(cardEntity);
        log.info("The card was saved successfully: {}", savedCardEntity.getId());
        clearCardCache(savedCardEntity.getId(),
                savedCardEntity.getUserId(),
                savedCardEntity.getAccountId());
        return cardMapper.toDTO(savedCardEntity);
    }

    @Transactional
    @Override
    public boolean deleteCardById(Long id) {
        log.info("Attempt to delete a card with an id {}", id);
        CardEntity entity = getEntityById(id);
        clearCardCache(entity.getId(), entity.getUserId(), entity.getAccountId());
        cardRepository.delete(entity);
        log.info("Card with id {} successfully deleted", id);
        return true;
    }

    @Transactional
    @Override
    public CardDTO updateCardById(Long id, Consumer<CardEntity> updateFunction) {
        log.info("Attempt to update card {}", id);
        CardEntity entity = getEntityById(id);

        Long userId = entity.getUserId();
        Long accountId = entity.getAccountId();

        updateFunction.accept(entity);
        CardEntity updatedEntity = cardRepository.save(entity);

        clearCardCache(id, userId, accountId);
        log.info("Card {} updated successfully", id);

        return cardMapper.toDTO(updatedEntity);

    }
    private void clearCardCache(Long cardId, Long userId, Long accountId) {
        redisService.delete(CARD_BY_ID_PREFIX + cardId);
        redisService.delete(CARD_BY_USER_PREFIX + userId);
        redisService.delete(CARD_BY_ACCOUNT_PREFIX + accountId);
        redisService.delete(ALL_CARDS_KEY);
        log.debug("Cleared cache for card {}", cardId);
    }


    private CardEntity getEntityById(Long id) {
        log.info("Attempt to get a card with an id {}", id);
        if (!Validator.isValidId(id)) {
            throw new BaseValidationException(ErrorCode.INVALID_CARD_ID);
        }
        return cardRepository.findById(id).orElseThrow(() -> new CardNotFoundException(ErrorCode.CARD_NOT_FOUND_BY_ID, id));
    }

    private CardEntity getEntityByUserId(Long id) {
        log.info("Attempt to get a card with user Id {}", id);
        if (!Validator.isValidId(id)) {
            throw new BaseValidationException(ErrorCode.INVALID_USER_ID);
        }
        return cardRepository.findCardEntityByUserId(id).orElseThrow(() -> new CardNotFoundException(ErrorCode.CARD_NOT_FOUND_BY_USER_ID, id));
    }

    private CardEntity getEntityByAccountId(Long id) {
        log.info("Attempt to get a card with an AccountId {}", id);
        if (!Validator.isValidId(id)) {
            throw new BaseValidationException(ErrorCode.INVALID_ACCOUNT_ID);
        }
        return cardRepository.findCardEntityByAccountId(id).orElseThrow(() -> new CardNotFoundException(ErrorCode.CARD_NOT_FOUND_BY_ACCOUNT_ID, id));
    }
}