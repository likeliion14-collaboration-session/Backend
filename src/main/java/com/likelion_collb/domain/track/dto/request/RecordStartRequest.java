package com.likelion_collb.domain.track.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class RecordStartRequest {

    @NotNull(message = "userId는 필수입니다.")
    private Long userId;
}
