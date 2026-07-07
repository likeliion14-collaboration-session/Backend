package com.likelion_collb.domain.card.dto.response;

import com.likelion_collb.domain.card.entity.Card;
import com.likelion_collb.domain.photo.entity.Photo;
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
public class CardCreateResponse {

    private Long cardId;
    private LocalDateTime createdAt;
    private List<CardCandidatesResponse.RouteDto> route;
    private List<HighlightPhotoDto> highlightPhotos;

    public static CardCreateResponse of(Card card, List<CardCandidatesResponse.RouteDto> route, List<Photo> highlightPhotos) {
        List<HighlightPhotoDto> photos = highlightPhotos.stream()
                .map(HighlightPhotoDto::from)
                .toList();

        return CardCreateResponse.builder()
                .cardId(card.getId())
                .createdAt(card.getCreatedAt())
                .route(route)
                .highlightPhotos(photos)
                .build();
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder
    public static class HighlightPhotoDto {

        private Long photoId;
        private String imageUrl;
        private Double latitude;
        private Double longitude;

        public static HighlightPhotoDto from(Photo photo) {
            return HighlightPhotoDto.builder()
                    .photoId(photo.getId())
                    .imageUrl(photo.getImgUrl())
                    .latitude(photo.getLatitude())
                    .longitude(photo.getLongitude())
                    .build();
        }
    }
}