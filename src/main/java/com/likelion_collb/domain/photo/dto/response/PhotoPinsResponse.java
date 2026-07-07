package com.likelion_collb.domain.photo.dto.response;

import com.likelion_collb.domain.photo.entity.Photo;
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
public class PhotoPinsResponse {

    private List<PinDto> photos;

    public static PhotoPinsResponse from(List<Photo> photos) {
        List<PinDto> pins = photos.stream()
                .map(PinDto::from)
                .toList();

        return PhotoPinsResponse.builder()
                .photos(pins)
                .build();
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder
    public static class PinDto {

        private Long photoId;
        private Double latitude;
        private Double longitude;
        private String pinIcon;

        public static PinDto from(Photo photo) {
            return PinDto.builder()
                    .photoId(photo.getId())
                    .latitude(photo.getLatitude())
                    .longitude(photo.getLongitude())
                    .pinIcon(photo.getPinIcon())
                    .build();
        }
    }
}