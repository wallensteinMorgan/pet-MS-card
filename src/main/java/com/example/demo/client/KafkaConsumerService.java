//package com.example.demo.client;
//
//import com.example.demo.dto.CardOperationInKafkaDTO;
//import com.example.demo.entity.CardOperationInKafkaEntity;
//import com.example.demo.mapper.CardOperationMapper;
//import com.example.demo.repository.CardOperationRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class KafkaConsumerService {
//    private final CardOperationRepository cardOperationRepository;
//    private final CardOperationMapper cardOperationMapper;
//
//    @KafkaListener(topics = "bank-card-topic", groupId = "bank-card-group")
//    public void listen(ConsumerRecord<String, CardOperationInKafkaDTO> record) {
//        CardOperationInKafkaDTO cardOperationInKafkaDTO = record.value();
//        log.info("Received message: {}", cardOperationInKafkaDTO);
//        CardOperationInKafkaEntity cardOperationInKafkaEntity = cardOperationMapper.toEntity(cardOperationInKafkaDTO);
//        cardOperationRepository.save(cardOperationInKafkaEntity);
//    }
//
//}