package com.example.demo.client;
import com.example.demo.dto.CardOperationInKafkaDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, CardOperationInKafkaDTO> kafkaTemplate;

    public void sendMessage(String topic, CardOperationInKafkaDTO message){
        log.info("Sending message to topic {}: {}", topic, message);
        kafkaTemplate.send(topic, message);
    }
}