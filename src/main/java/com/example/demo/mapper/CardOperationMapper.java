package com.example.demo.mapper;

import com.example.demo.dto.CardOperationInKafkaDTO;
import com.example.demo.entity.CardOperationInKafkaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CardOperationMapper {
    CardOperationInKafkaEntity toEntity(CardOperationInKafkaDTO dto);


    CardOperationInKafkaDTO toDTO(CardOperationInKafkaEntity entity);
}
