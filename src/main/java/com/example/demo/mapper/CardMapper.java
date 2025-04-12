package com.example.demo.mapper;

import com.example.demo.dto.CardDTO;
import com.example.demo.entity.CardEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {CardTypeMapper.class, PaymentSystemMapper.class})
public interface CardMapper {
    @Mapping(source = "cardNumber", target = "cardNumber", qualifiedByName = "mapCardNumber")
    CardEntity toEntity(CardDTO cardDTO);

    CardDTO toDTO(CardEntity cardEntity);
    @Named("mapCardNumber")
    static String mapCardNumber(String cardNumber) {
        cardNumber = cardNumber.replaceAll("\\s", "");
        cardNumber = cardNumber.replaceAll("-", "");
        cardNumber = maskCardNumber(cardNumber);
        return cardNumber;
    }

    static String maskCardNumber(String cardNumber) {
        return "************" + cardNumber.substring(12);
    }
}