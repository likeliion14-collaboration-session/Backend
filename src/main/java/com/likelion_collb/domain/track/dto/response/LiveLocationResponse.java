package com.likelion_collb.domain.track.dto.response;

import com.likelion_collb.domain.track.entity.LiveLocation;
import lombok.*;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class LiveLocationResponse {
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private Double latitude;
    private Double longitude;

    public static LiveLocationResponse from(LiveLocation liveLocation) {
        return LiveLocationResponse.builder()
                .userId(liveLocation.getUser().getId())
                .nickname(liveLocation.getUser().getNickname())
                .profileImageUrl(liveLocation.getUser().getProfileImageUrl())
                .latitude(liveLocation.getLatitude())
                .longitude(liveLocation.getLongitude())
                .build();
    }
}
