package com.likelion_collb.domain.user.dto.response;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class LoginResponse {

    private Long id;
    private String nickname;
    private String profileImageUrl;
    private Boolean isNewUser;
}
