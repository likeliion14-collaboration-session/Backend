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


}
