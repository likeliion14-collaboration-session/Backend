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
public class PhotoDeleteResponse {

    private Long photoId;
    private LocalDateTime deletedAt;

    public static PhotoDeleteResponse from(Photo photo) {
        return PhotoDeleteResponse.builder()
                .photoId(photo.getId())
                .deletedAt(photo.getDeletedAt())
                .build();
    }
}