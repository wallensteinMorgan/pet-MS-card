package com.example.demo.repository;

import com.example.demo.entity.CardOperationInKafkaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CardOperationRepository extends JpaRepository<CardOperationInKafkaEntity, Long> {
}
