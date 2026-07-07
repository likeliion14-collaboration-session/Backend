package com.likelion_collb.domain.connection.exception;

import com.likelion_collb.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum ConnectionErrorCode implements ErrorCode {

    CONNECTION_NOT_FOUND("CONNECTION_001", "연동 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOT_A_MEMBER("CONNECTION_002", "해당 연동에 속한 사용자가 아닙니다.", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus status;
}