package com.likelion_collb.domain.track.dto;

import com.likelion_collb.domain.track.entity.TrackPoint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class TrackPointsResponse {

    private Long userId;
    private List<PointDto> points;
    private boolean isPartiallyShared;
    private LocalDateTime sharedUntil;

    public static TrackPointsResponse from(Long userId, List<TrackPoint> trackPoints,
                                           boolean isPartiallyShared, LocalDateTime sharedUntil) {
        List<PointDto> points = trackPoints.stream()
                .map(PointDto::from)
                .toList();

        return TrackPointsResponse.builder()
                .userId(userId)
                .points(points)
                .isPartiallyShared(isPartiallyShared)
                .sharedUntil(sharedUntil)
                .build();
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder
    public static class PointDto {

        private Double latitude;
        private Double longitude;
        private LocalDateTime recordedAt;

        public static PointDto from(TrackPoint trackPoint) {
            return PointDto.builder()
                    .latitude(trackPoint.getLatitude())
                    .longitude(trackPoint.getLongitude())
                    .recordedAt(trackPoint.getCreatedAt())
                    .build();
        }
    }
}