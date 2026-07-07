package com.likelion_collb.domain.track.exception;

import com.likelion_collb.global.exception.ErrorCode;
import lombok.Getter;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public enum TrackErrorCode implements ErrorCode {

    INVALID_COORDINATE("TRACK_001", "위도/경도 값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    LIVE_LOCATION_NOT_FOUND("TRACK_002", "현재 위치 정보가 없습니다.", HttpStatus.NOT_FOUND),
    ALREADY_STARTED("TRACK_003", "오늘은 이미 기록을 시작했습니다.", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus status;
}