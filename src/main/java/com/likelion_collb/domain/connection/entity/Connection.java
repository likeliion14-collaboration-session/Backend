package com.likelion_collb.domain.connection.entity;

import com.likelion_collb.domain.user.entity.User;
import com.likelion_collb.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id_a", "user_id_b"}))
public class Connection extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_a")
    private User userA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_b")
    private User userB;


    // 요청자(requesterId) 입장에서 상대방이 누구인지 판단하는 편의 메서드
    public User getPartner(Long myUserId) {
        if (userA.getId().equals(myUserId)) {
            return userB;
        }
        return userA;
    }
}