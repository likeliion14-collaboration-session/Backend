package com.likelion_collb.domain.share.dto.response;

import com.likelion_collb.domain.share.entity.Share;
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
public class SharingToggleResponse {

    private Long connectionId;
    private Long ownerUserId;
    private boolean isSharingEnabled;
    private LocalDateTime disabledAt;

    public static SharingToggleResponse from(Share share) {
        return SharingToggleResponse.builder()
                .connectionId(share.getConnection().getId())
                .ownerUserId(share.getUser().getId())
                .isSharingEnabled(share.isShared())
                .disabledAt(share.getDisabledAt())
                .build();
    }
}