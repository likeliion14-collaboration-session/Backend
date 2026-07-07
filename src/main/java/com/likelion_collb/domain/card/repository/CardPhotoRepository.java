package com.likelion_collb.domain.card.repository;

import com.likelion_collb.domain.card.entity.CardPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardPhotoRepository extends JpaRepository<CardPhoto, Long> {

    List<CardPhoto> findAllByCardId(Long cardId);
}