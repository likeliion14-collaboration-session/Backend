package com.likelion_collb.domain.track.controller;

import com.likelion_collb.domain.track.dto.request.LocationSaveRequest;
import com.likelion_collb.domain.track.dto.request.RecordStartRequest;
import com.likelion_collb.domain.track.dto.response.LiveLocationResponse;
import com.likelion_collb.domain.track.dto.response.LocationSaveResponse;
import com.likelion_collb.domain.track.dto.response.RecordStartResponse;
import com.likelion_collb.domain.track.dto.response.TrackPointsResponse;
import com.likelion_collb.domain.track.service.TrackService;
import com.likelion_collb.global.entity.BaseTimeEntity;
import com.likelion_collb.global.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class TrackController {


    private final TrackService trackService;

    //기록 시작
    /**
     * @param request: userID
     */
    @PostMapping("/records/start")
    public ResponseEntity<BaseResponse<RecordStartResponse>> startRecord(
            @Valid @RequestBody RecordStartRequest request) {

        RecordStartResponse response = trackService.startRecord(request.getUserId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(response));
    }


    //위치 저장 (3초 폴링 이용, liveLocation과 trackPoint 모두 업데이트)

    /**
     * @param request: userId, latitude, longitude
     * @return
     */
    @PostMapping("/locations")
    public ResponseEntity<BaseResponse<LocationSaveResponse>> saveLocation(@RequestBody @Valid LocationSaveRequest request) {

        LocationSaveResponse locationSaveResponse = trackService
                .saveLocation(request.getUserId(), request.getLatitude(), request.getLongitude());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(locationSaveResponse));

    }

    // 동선 좌표 목록 조회 (targetId 생략 시 본인 조회)

    /**
     * @param requesterId
     * @param targetId
     * @param date
     * @return
     */
    @GetMapping("/track-points")
    public ResponseEntity<BaseResponse<TrackPointsResponse>> getTrackPoints(
            @RequestParam Long requesterId,
            @RequestParam(required = false) Long targetId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        TrackPointsResponse response = trackService.getTrackPoints(requesterId, targetId, date);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(response));
    }

    // 현재 위치 조회 (targetId 생략 시 본인 조회)

    /**
     * @param requesterId
     * @param targetId
     * @return
     */
    @GetMapping("/users/live-location")
    public ResponseEntity<BaseResponse<LiveLocationResponse>> getLiveLocation(
            @RequestParam Long requesterId,
            @RequestParam(required = false) Long targetId) {

        LiveLocationResponse response = trackService.getLiveLocation(requesterId, targetId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(response));
    }
}
