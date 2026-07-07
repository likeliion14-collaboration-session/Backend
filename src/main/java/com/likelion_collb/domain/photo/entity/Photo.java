package com.likelion_collb.domain.photo.entity;

import com.likelion_collb.domain.user.entity.User;
import com.likelion_collb.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Photo extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String imgUrl;

    private Double latitude;

    private Double longitude;

    private LocalDateTime takenAt;   // EXIF 촬영시각

    private String comment;

    private String pinIcon;

    private LocalDateTime deletedAt;

    // 코멘트/아이콘 수정
    public void updateContent(String comment, String pinIcon) {
        this.comment = comment;
        this.pinIcon = pinIcon;
    }

    // 소프트 삭제
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    // 본인 사진인지 확인 (Service에서 쓸 편의 메서드)
    public boolean isOwnedBy(Long userId) {
        return this.user.getId().equals(userId);
    }

    // 위치 정보 존재 여부 (hasLocation 응답 필드용)
    public boolean hasLocation() {
        return this.latitude != null && this.longitude != null;
    }
}
