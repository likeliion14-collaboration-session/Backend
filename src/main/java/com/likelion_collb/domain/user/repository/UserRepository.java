package com.likelion_collb.domain.user.repository;

import com.likelion_collb.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByNickname(String name);
}
