package com.example.demo.repository;


import com.example.demo.entity.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<CardEntity, Long> {
    Optional<CardEntity> findCardEntityByAccountId(Long userId);

    Optional<CardEntity> findCardEntityByUserId(Long userId);
}