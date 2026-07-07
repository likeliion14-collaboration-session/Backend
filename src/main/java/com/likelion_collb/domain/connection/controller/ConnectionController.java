package com.likelion_collb.domain.connection.controller;

import com.likelion_collb.domain.connection.dto.response.ConnectionListResponse;
import com.likelion_collb.domain.connection.service.ConnectionService;
import com.likelion_collb.domain.share.dto.request.SharingToggleRequest;
import com.likelion_collb.domain.share.dto.response.SharingToggleResponse;
import com.likelion_collb.domain.share.service.ShareService;
import com.likelion_collb.global.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ConnectionController {

    private final ConnectionService connectionService;
    private final ShareService shareService;

     /**
     *      * 연동 목록 조회
     *      * @param userId (Long, path variable): 연동 목록을 조회할 사용자 식별자
     */
    @GetMapping("/users/{userId}/connections")
    public ResponseEntity<BaseResponse<ConnectionListResponse>> getConnections(
            @PathVariable Long userId) {

        ConnectionListResponse response = connectionService.getConnections(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(response));
    }

    /**
     * 공유 켜기/끄기
     * @param connectionId (Long, path variable): 대상 연동 식별자
     * @param request (JSON body)
     *   - userId (Long, 필수): 요청자 식별자 (본인의 공유 설정만 변경)
     *   - enabled (Boolean, 필수): true면 공유 켜기, false면 끄기
     */
    @PatchMapping("/connections/{connectionId}/sharing")
    public ResponseEntity<BaseResponse<SharingToggleResponse>> toggleSharing(
            @PathVariable Long connectionId,
            @Valid @RequestBody SharingToggleRequest request) {

        SharingToggleResponse response = shareService.toggleSharing(connectionId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(response));
    }
}