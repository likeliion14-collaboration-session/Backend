package com.likelion_collb.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    private MultipartFile profileImage;
}
