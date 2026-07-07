package com.likelion_collb.domain.photo.dto.response;

import com.likelion_collb.domain.photo.entity.Photo;
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
public class PhotoDetailResponse {

    private Long photoId;
    private String imageUrl;
    private Double latitude;
    private Double longitude;
    private String comment;
    private String pinIcon;
    private LocalDateTime takenAt;
    private LocalDateTime uploadedAt;

    public static PhotoDetailResponse from(Photo photo) {
        return PhotoDetailResponse.builder()
                .photoId(photo.getId())
                .imageUrl(photo.getImgUrl())
                .latitude(photo.getLatitude())
                .longitude(photo.getLongitude())
                .comment(photo.getComment())
                .pinIcon(photo.getPinIcon())
                .takenAt(photo.getTakenAt())
                .uploadedAt(photo.getCreatedAt())
                .build();
    }
}