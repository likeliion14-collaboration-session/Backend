package com.likelion_collb.domain.photo.exception;

import com.likelion_collb.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum PhotoErrorCode implements ErrorCode {

    INVALID_FILE_TYPE("PHOTO_001", "지원하지 않는 파일 형식입니다. (jpg, jpeg, png만 가능)", HttpStatus.BAD_REQUEST),
    PHOTO_NOT_FOUND("PHOTO_002", "사진을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FILE_SAVE_FAILED("PHOTO_003", "사진 저장 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    EMPTY_FILE("PHOTO_004", "업로드된 파일이 비어있거나 손상되었습니다.", HttpStatus.BAD_REQUEST),
    RECORD_NOT_STARTED("PHOTO_005", "기록을 시작해야 사진을 업로드할 수 있습니다.", HttpStatus.BAD_REQUEST),
    FORBIDDEN("PHOTO_006", "본인의 사진만 수정하거나 삭제할 수 있습니다.", HttpStatus.FORBIDDEN);


    private final String code;
    private final String message;
    private final HttpStatus status;
}