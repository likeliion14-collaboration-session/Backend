package com.likelion_collb.domain.track.dto.response;

import com.likelion_collb.domain.track.entity.TrackPoint;
import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TrackPointsResponse {
    private Long userId;
    private List<PointDto> points;

    public static TrackPointsResponse from(Long userId, List<TrackPoint> trackPoints) {
        List<PointDto> points = trackPoints.stream()
                .map(PointDto::from)
                .toList();

        return TrackPointsResponse.builder()
                .userId(userId)
                .points(points)
                .build();
    }
        @Getter
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        @AllArgsConstructor(access = AccessLevel.PROTECTED)
        @Builder
        public static class PointDto {

            private Double latitude;
            private Double longitude;
            private java.time.LocalDateTime recordedAt;

            public static PointDto from(TrackPoint trackPoint) {
                return PointDto.builder()
                        .latitude(trackPoint.getLatitude())
                        .longitude(trackPoint.getLongitude())
                        .recordedAt(trackPoint.getCreatedAt())
                        .build();
            }
        }
}
