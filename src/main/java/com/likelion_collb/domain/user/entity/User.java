package com.likelion_collb.domain.user.entity;

import com.likelion_collb.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nickname;

    private LocalDateTime recordStartedAt;

    private LocalDateTime deletedAt;

    private String profileImageUrl;

    //기록 시작 매서드
    public void startRecord() {
        this.recordStartedAt = LocalDateTime.now();
    }


}
