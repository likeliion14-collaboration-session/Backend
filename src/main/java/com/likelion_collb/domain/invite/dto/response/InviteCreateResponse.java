package com.likelion_collb.domain.invite.dto.response;

import com.likelion_collb.domain.invite.entity.Invite;
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
public class InviteCreateResponse {

    private Long inviteId;
    private String inviteCode;
    private LocalDateTime expiresAt;

    public static InviteCreateResponse from(Invite invite) {
        return InviteCreateResponse.builder()
                .inviteId(invite.getId())
                .inviteCode(invite.getCode())
                .expiresAt(invite.getExpiresAt())
                .build();
    }
}