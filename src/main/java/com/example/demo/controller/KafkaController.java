package com.example.demo.controller;

import com.example.demo.client.KafkaProducerService;
import com.example.demo.dto.CardOperationInKafkaDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/kafka")
@RequiredArgsConstructor
public class KafkaController {
    private final KafkaProducerService kafkaProducerService;
    @PostMapping("/send/{topic}")
    public String sendMessageToKafka(@PathVariable String topic, @RequestBody @Valid CardOperationInKafkaDTO
            cardOperationInKafkaDTO){
        log.info("Received request to send message to Kafka topic '{}': {}", topic, cardOperationInKafkaDTO);
        if (cardOperationInKafkaDTO.getTimestamp() == null) {
            cardOperationInKafkaDTO.setTimestamp(java.time.LocalDateTime.now());
        }
        kafkaProducerService.sendMessage(topic, cardOperationInKafkaDTO);
        return "Message sent to Kafka topic: " + topic;
    }
}