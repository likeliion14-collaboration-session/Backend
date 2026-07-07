package com.likelion_collb.domain.share.exception;

import com.likelion_collb.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum ShareErrorCode implements ErrorCode {

    SHARE_NOT_FOUND("SHARE_001", "공유 설정을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus status;
}