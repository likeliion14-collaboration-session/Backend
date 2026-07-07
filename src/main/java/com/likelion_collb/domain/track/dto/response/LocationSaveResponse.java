package com.likelion_collb.domain.track.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class LocationSaveResponse {
    private Long trackPointId;

    private Double latitude;

    private Double longitude;

    private LocalDateTime recordedAt;
}
