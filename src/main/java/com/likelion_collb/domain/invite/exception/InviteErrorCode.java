package com.likelion_collb.domain.invite.exception;

import com.likelion_collb.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum InviteErrorCode implements ErrorCode {

    INVALID_INVITE_CODE("INVITE_001", "유효하지 않은 초대 코드입니다.", HttpStatus.BAD_REQUEST),
    ALREADY_USED_CODE("INVITE_002", "이미 사용된 초대 코드입니다.", HttpStatus.CONFLICT),
    EXPIRED_INVITE_CODE("INVITE_003", "만료된 초대 코드입니다.", HttpStatus.BAD_REQUEST),
    CANNOT_CONNECT_SELF("INVITE_004", "자기 자신과는 연동할 수 없습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_CONNECTED("INVITE_005", "이미 연동된 사용자입니다.", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus status;
}