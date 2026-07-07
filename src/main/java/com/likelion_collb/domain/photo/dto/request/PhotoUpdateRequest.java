package com.likelion_collb.domain.photo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Setter
public class PhotoUpdateRequest {

    @NotNull(message = "userId는 필수입니다.")
    private Long userId;

    @Size(max = 45, message = "코멘트는 45자를 넘을 수 없습니다.")
    private String comment;

    private String pinIcon;
}