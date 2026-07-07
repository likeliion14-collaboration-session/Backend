package com.likelion_collb.domain.invite.dto.response;

import com.likelion_collb.domain.connection.entity.Connection;
import com.likelion_collb.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class InviteAcceptResponse {

    private Long connectionId;
    private PartnerDto partner;
    private LocalDateTime connectedAt;

    public static InviteAcceptResponse of(Connection connection, User partner) {
        return InviteAcceptResponse.builder()
                .connectionId(connection.getId())
                .partner(PartnerDto.from(partner))
                .connectedAt(connection.getCreatedAt())
                .build();
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder
    public static class PartnerDto {

        private Long userId;
        private String nickname;

        public static PartnerDto from(User user) {
            return PartnerDto.builder()
                    .userId(user.getId())
                    .nickname(user.getNickname())
                    .build();
        }
    }
}