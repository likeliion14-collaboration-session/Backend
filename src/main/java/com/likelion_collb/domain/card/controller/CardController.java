package com.likelion_collb.domain.card.controller;

import com.likelion_collb.domain.card.dto.request.CardCreateRequest;
import com.likelion_collb.domain.card.dto.response.CardCandidatesResponse;
import com.likelion_collb.domain.card.dto.response.CardCreateResponse;
import com.likelion_collb.domain.card.service.CardService;
import com.likelion_collb.global.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/card")
public class CardController {

    private final CardService cardService;

    /**
     * 1단계: 카드 후보 조회
     * @param userId (Long, 필수): 사용자 식별자
     * @param until (LocalDateTime, 필수): 조회 범위 끝 시각 (ISO-8601, 예: 2026-07-05T15:30:00)
     */
    @GetMapping("/candidates")
    public ResponseEntity<BaseResponse<CardCandidatesResponse>> getCandidates(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime until) {

        CardCandidatesResponse response = cardService.getCandidates(userId, until);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(response));
    }

    /**
     * 2단계: 카드 확정 발급
     * @param request (JSON body)
     *   - userId (Long, 필수): 사용자 식별자
     *   - until (LocalDateTime, 필수): 조회 범위 끝 시각
     *   - selectedPhotoIds (List<Long>, 선택): 대표 사진으로 선택한 사진 id 목록 (최대 3개, 없으면 빈 배열/생략 가능)
     */
    @PostMapping
    public ResponseEntity<BaseResponse<CardCreateResponse>> createCard(
            @Valid @RequestBody CardCreateRequest request) {

        CardCreateResponse response = cardService.createCard(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(response));
    }
}