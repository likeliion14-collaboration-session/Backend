package com.likelion_collb.domain.card.dto.response;

import com.likelion_collb.domain.photo.entity.Photo;
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
public class CardCandidatesResponse {

    private List<RouteDto> route;
    private List<PhotoCandidateDto> photos;

    public static CardCandidatesResponse of(List<TrackPoint> trackPoints, List<Photo> photos) {
        List<RouteDto> route = trackPoints.stream()
                .map(RouteDto::from)
                .toList();

        List<PhotoCandidateDto> photoCandidates = photos.stream()
                .map(PhotoCandidateDto::from)
                .toList();

        return CardCandidatesResponse.builder()
                .route(route)
                .photos(photoCandidates)
                .build();
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder
    public static class RouteDto {

        private Double latitude;
        private Double longitude;
        private LocalDateTime recordedAt;

        public static RouteDto from(TrackPoint trackPoint) {
            return RouteDto.builder()
                    .latitude(trackPoint.getLatitude())
                    .longitude(trackPoint.getLongitude())
                    .recordedAt(trackPoint.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder
    public static class PhotoCandidateDto {

        private Long photoId;
        private String imageUrl;
        private Double latitude;
        private Double longitude;
        private String comment;
        private String pinIcon;

        public static PhotoCandidateDto from(Photo photo) {
            return PhotoCandidateDto.builder()
                    .photoId(photo.getId())
                    .imageUrl(photo.getImgUrl())
                    .latitude(photo.getLatitude())
                    .longitude(photo.getLongitude())
                    .comment(photo.getComment())
                    .pinIcon(photo.getPinIcon())
                    .build();
        }
    }
}