package com.likelion_collb.domain.photo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Setter
public class PhotoUploadRequest {

    @NotNull(message = "userId는 필수입니다.")
    private Long userId;

    private MultipartFile image;

    @Size(max = 45, message = "코멘트는 45자를 넘을 수 없습니다.")
    private String comment;

    private String pinIcon;
}