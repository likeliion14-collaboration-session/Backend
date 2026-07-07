package com.likelion_collb.domain.track.repository;

import com.likelion_collb.domain.track.entity.TrackPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TrackPointRepository extends JpaRepository<TrackPoint, Long> {

    //측정 시작부터 끝날떄까지 시간 순 조회 매서드
    @Query("SELECT tp FROM TrackPoint tp " +
            "WHERE tp.user.id = :userId " +
            "AND tp.createdAt BETWEEN :start AND :end " +
            "ORDER BY tp.createdAt ASC")
    List<TrackPoint> findTrackPoints(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
