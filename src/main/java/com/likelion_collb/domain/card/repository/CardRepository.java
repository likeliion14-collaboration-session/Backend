package com.likelion_collb.domain.card.repository;

import com.likelion_collb.domain.card.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
}