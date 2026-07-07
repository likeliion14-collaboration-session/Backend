package com.likelion_collb.domain.user.exception;

import com.likelion_collb.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND("USER_001", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_NICKNAME("USER_002", "닉네임 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_PROFILE_IMAGE_TYPE("USER_003", "지원하지 않는 이미지 형식입니다. (jpg, jpeg, png만 가능)", HttpStatus.BAD_REQUEST),
    PROFILE_IMAGE_SAVE_FAILED("USER_004", "프로필 이미지 저장 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
