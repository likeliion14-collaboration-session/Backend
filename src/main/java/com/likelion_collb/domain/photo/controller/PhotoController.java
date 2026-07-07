package com.likelion_collb.domain.photo.controller;

import com.likelion_collb.domain.photo.dto.request.PhotoUpdateRequest;
import com.likelion_collb.domain.photo.dto.request.PhotoUploadRequest;
import com.likelion_collb.domain.photo.dto.response.*;
import com.likelion_collb.domain.photo.service.PhotoService;
import com.likelion_collb.global.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/photos")
public class PhotoController {

    private final PhotoService photoService;

    /**
     * 사진 업로드
     * @param request (multipart/form-data)
     *   - userId (Long, 필수): 업로드하는 사용자 식별자
     *   - image (MultipartFile, 필수): 업로드할 이미지 파일 (jpg, jpeg, png만 허용)
     *   - comment (String, 선택): 사진 코멘트 (최대 200자)
     *   - pinIcon (String, 선택): 지도에 표시할 핀 아이콘 (정해진 목록 중 하나)
     * 전제 조건: userId가 오늘 "기록 시작"을 한 상태여야 함 (안 했으면 RECORD_NOT_STARTED)
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<BaseResponse<PhotoUploadResponse>> upload(
            @Valid @ModelAttribute PhotoUploadRequest request) {

        PhotoUploadResponse response = photoService.upload(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(response));
    }

    /**
     * 사진 핀 목록 조회 (지도 표시용, 가벼운 데이터)
     * @param requesterId (Long, 필수): 조회를 요청하는 사용자 (나)
     * @param targetId (Long, 선택): 조회 대상 사용자. 생략 시 requesterId와 동일하게 처리(본인 조회)
     */
    @GetMapping("/pins")
    public ResponseEntity<BaseResponse<PhotoPinsResponse>> getPins(
            @RequestParam Long requesterId,
            @RequestParam(required = false) Long targetId) {

        PhotoPinsResponse response = photoService.getPins(requesterId, targetId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(response));
    }

    /**
     * 사진 상세 조회 (핀 클릭 시)
     * @param photoId (Long, path variable): 조회할 사진 식별자
     */
    @GetMapping("/{photoId}")
    public ResponseEntity<BaseResponse<PhotoDetailResponse>> getDetail(
            @PathVariable Long photoId) {

        PhotoDetailResponse response = photoService.getDetail(photoId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(response));
    }

    /**
     * 사진 코멘트/아이콘 수정 (본인 사진만 가능)
     * @param photoId (Long, path variable): 수정할 사진 식별자
     * @param request (JSON body)
     *   - userId (Long, 필수): 요청자 식별자 (사진 소유자와 일치해야 함, 아니면 FORBIDDEN)
     *   - comment (String, 선택): 수정할 코멘트 (최대 200자)
     *   - pinIcon (String, 선택): 수정할 핀 아이콘
     */
    @PatchMapping("/{photoId}")
    public ResponseEntity<BaseResponse<PhotoUpdateResponse>> update(
            @PathVariable Long photoId,
            @Valid @RequestBody PhotoUpdateRequest request) {

        PhotoUpdateResponse response = photoService.update(photoId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(response));
    }

    /**
     * 사진 삭제 (소프트 삭제, 본인 사진만 가능)
     * @param photoId (Long, path variable): 삭제할 사진 식별자
     * @param userId (Long, query parameter): 요청자 식별자 (사진 소유자와 일치해야 함, 아니면 FORBIDDEN)
     */
    @DeleteMapping("/{photoId}")
    public ResponseEntity<BaseResponse<PhotoDeleteResponse>> delete(
            @PathVariable Long photoId,
            @RequestParam Long userId) {

        PhotoDeleteResponse response = photoService.delete(photoId, userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(response));
    }
}