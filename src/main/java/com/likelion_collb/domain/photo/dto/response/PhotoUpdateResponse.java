package com.likelion_collb.domain.photo.dto.response;

import com.likelion_collb.domain.photo.entity.Photo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class PhotoUpdateResponse {

    private Long photoId;
    private String comment;
    private String pinIcon;

    public static PhotoUpdateResponse from(Photo photo) {
        return PhotoUpdateResponse.builder()
                .photoId(photo.getId())
                .comment(photo.getComment())
                .pinIcon(photo.getPinIcon())
                .build();
    }
}