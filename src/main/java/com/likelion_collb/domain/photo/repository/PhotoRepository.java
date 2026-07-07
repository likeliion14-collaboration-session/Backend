package com.likelion_collb.domain.photo.repository;

import com.likelion_collb.domain.photo.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

    // 핀 목록 조회용 - 특정 유저의 삭제되지 않은, 위치 있는 사진만
    @Query("SELECT p FROM Photo p " +
            "WHERE p.user.id = :userId " +
            "AND p.deletedAt IS NULL " +
            "AND p.latitude IS NOT NULL " +
            "AND p.longitude IS NOT NULL")
    List<Photo> findPhotoPins(@Param("userId") Long userId);

    // 상세 조회용 - 삭제되지 않은 것만
    @Query("SELECT p FROM Photo p WHERE p.id = :photoId AND p.deletedAt IS NULL")
    Optional<Photo> findByIdAndNotDeleted(@Param("photoId") Long photoId);

    // 카드 후보 조회용 - 특정 유저의 삭제되지 않은 사진 전체 (위치 없어도 포함)
    @Query("SELECT p FROM Photo p WHERE p.user.id = :userId AND p.deletedAt IS NULL")
    List<Photo> findAllByUserIdAndNotDeleted(@Param("userId") Long userId);

    @Query("SELECT p FROM Photo p " +
            "WHERE p.user.id = :userId " +
            "AND p.deletedAt IS NULL " +
            "AND p.createdAt BETWEEN :start AND :end")
    List<Photo> findAllByUserIdAndNotDeletedBetween(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

}
