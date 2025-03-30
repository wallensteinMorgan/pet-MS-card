package com.example.demo.mapper;

import com.example.demo.entity.CardType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CardTypeMapper {

    String toString(CardType cardType);

    CardType toCardType(String cardType);
}
