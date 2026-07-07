package com.likelion_collb.domain.invite.controller;

import com.likelion_collb.domain.invite.dto.request.InviteAcceptRequest;
import com.likelion_collb.domain.invite.dto.response.InviteAcceptResponse;
import com.likelion_collb.domain.invite.dto.response.InviteCreateResponse;
import com.likelion_collb.domain.invite.service.InviteService;
import com.likelion_collb.global.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/invites")
public class InviteController {

    private final InviteService inviteService;

    /**
     * 초대 코드 생성
     * @param userId (Long, query parameter, 필수): 초대를 만드는 사용자 식별자
     */
    @PostMapping
    public ResponseEntity<BaseResponse<InviteCreateResponse>> createInvite(
            @RequestParam Long userId) {

        InviteCreateResponse response = inviteService.createInvite(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(response));
    }

    /**
     * 초대 코드 입력하여 연동
     * @param request (JSON body)
     *   - userId (Long, 필수): 코드를 입력하는 사용자 식별자
     *   - inviteCode (String, 필수): 전달받은 초대 코드
     */
    @PostMapping("/accept")
    public ResponseEntity<BaseResponse<InviteAcceptResponse>> acceptInvite(
            @Valid @RequestBody InviteAcceptRequest request) {

        InviteAcceptResponse response = inviteService.acceptInvite(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(response));
    }
}