package com.likelion_collb.domain.card.exception;

import com.likelion_collb.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum CardErrorCode implements ErrorCode {

    INVALID_UNTIL_PARAM("CARD_001", "조회 범위(until) 값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    FUTURE_TIME_NOT_ALLOWED("CARD_002", "미래 시점은 조회할 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_PHOTO_COUNT("CARD_003", "대표 사진은 정확히 3장 선택해야 합니다.", HttpStatus.BAD_REQUEST),
    NOT_YOUR_PHOTO("CARD_004", "본인이 업로드한 사진만 선택할 수 있습니다.", HttpStatus.BAD_REQUEST),
    PHOTO_OUT_OF_RANGE("CARD_005", "선택한 사진이 조회 범위를 벗어났습니다.", HttpStatus.BAD_REQUEST),
    RECORD_NOT_STARTED("CARD_006", "오늘 기록을 시작해야 카드를 만들 수 있습니다.", HttpStatus.BAD_REQUEST);
    private final String code;
    private final String message;
    private final HttpStatus status;
}