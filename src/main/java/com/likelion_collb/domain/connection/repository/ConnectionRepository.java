package com.likelion_collb.domain.connection.repository;

import com.likelion_collb.domain.connection.entity.Connection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    // 두 유저 사이에 이미 연동이 있는지 확인 (양방향 다 체크)
    @Query("SELECT c FROM Connection c " +
            "WHERE (c.userA.id = :userIdA AND c.userB.id = :userIdB) " +
            "OR (c.userA.id = :userIdB AND c.userB.id = :userIdA)")
    Optional<Connection> findByUsers(@Param("userIdA") Long userIdA, @Param("userIdB") Long userIdB);

    // 나와 연동된 모든 Connection 조회 (내가 A든 B든 다 포함)
    @Query("SELECT c FROM Connection c WHERE c.userA.id = :userId OR c.userB.id = :userId")
    List<Connection> findAllByUserId(@Param("userId") Long userId);
}