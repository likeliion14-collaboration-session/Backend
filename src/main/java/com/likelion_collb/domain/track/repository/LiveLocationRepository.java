package com.likelion_collb.domain.track.repository;

import com.likelion_collb.domain.track.entity.LiveLocation;
import com.likelion_collb.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LiveLocationRepository extends JpaRepository<LiveLocation, Long> {

    Optional<LiveLocation> findByUser(User user);

    Optional<LiveLocation> findByUserId(Long userId);
}
