package com.likelion_collb.domain.share.entity;

import com.likelion_collb.domain.connection.entity.Connection;
import com.likelion_collb.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_share_connection_user", columnNames = {"connection_id", "user_id"}))
public class Share {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "connection_id")
    private Connection connection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    @Builder.Default
    private boolean isShared = true;

    private LocalDateTime disabledAt;

    // 공유 끄기
    public void disable() {
        this.isShared = false;
        this.disabledAt = LocalDateTime.now();
    }

    // 공유 켜기 (컷오프 해제, 끈 기간 기록도 다시 보이게)
    public void enable() {
        this.isShared = true;
        this.disabledAt = null;
    }
}