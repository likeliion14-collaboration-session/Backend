package com.likelion_collb.domain.share.repository;

import com.likelion_collb.domain.share.entity.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ShareRepository extends JpaRepository<Share, Long> {

    // 특정 connection에서, 특정 유저(owner) 입장의 공유 설정 조회
    @Query("SELECT s FROM Share s WHERE s.connection.id = :connectionId AND s.user.id = :userId")
    Optional<Share> findByConnectionIdAndUserId(@Param("connectionId") Long connectionId, @Param("userId") Long userId);
}