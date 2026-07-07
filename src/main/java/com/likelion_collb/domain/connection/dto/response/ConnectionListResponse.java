package com.likelion_collb.domain.connection.dto.response;

import com.likelion_collb.domain.connection.entity.Connection;
import com.likelion_collb.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ConnectionListResponse {

    private List<ConnectionDto> connections;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder
    public static class ConnectionDto {

        private Long connectionId;
        private PartnerDto partner;
        private boolean isSharingEnabled;

        public static ConnectionDto of(Connection connection, User partner, boolean isShared) {
            return ConnectionDto.builder()
                    .connectionId(connection.getId())
                    .partner(PartnerDto.from(partner))
                    .isSharingEnabled(isShared)
                    .build();
        }
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