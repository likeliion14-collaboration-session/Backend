package com.likelion_collb.domain.track.service;

import com.likelion_collb.domain.connection.service.ConnectionAccessService;
import com.likelion_collb.domain.track.dto.TrackPointsResponse;
import com.likelion_collb.domain.track.dto.response.LocationSaveResponse;

import com.likelion_collb.domain.track.dto.response.LiveLocationResponse;
import com.likelion_collb.domain.track.dto.response.RecordStartResponse;
import com.likelion_collb.domain.track.entity.LiveLocation;
import com.likelion_collb.domain.track.entity.TrackPoint;
import com.likelion_collb.domain.track.exception.TrackErrorCode;
import com.likelion_collb.domain.track.repository.LiveLocationRepository;
import com.likelion_collb.domain.track.repository.TrackPointRepository;
import com.likelion_collb.domain.user.entity.User;
import com.likelion_collb.domain.user.exception.UserErrorCode;
import com.likelion_collb.domain.user.repository.UserRepository;
import com.likelion_collb.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrackService {

    private final TrackPointRepository trackPointRepository;
    private final LiveLocationRepository liveLocationRepository;
    private final UserRepository userRepository;
    private final ConnectionAccessService connectionAccessService;

    @Transactional
    public RecordStartResponse startRecord(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        if (user.getRecordStartedAt() != null
                && user.getRecordStartedAt().toLocalDate().equals(LocalDate.now())) {
            throw new CustomException(TrackErrorCode.ALREADY_STARTED);
        }

        user.startRecord();

        return RecordStartResponse.builder()
                .recordStartedAt(user.getRecordStartedAt())
                .build();
    }

    @Transactional
    public LocationSaveResponse saveLocation(Long userId, Double latitude, Double longitude) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        LiveLocation liveLocation = liveLocationRepository.findByUser(user).orElse(null);

        if (liveLocation == null) {
            liveLocation = LiveLocation.builder()
                    .user(user)
                    .latitude(latitude)
                    .longitude(longitude)
                    .build();
            liveLocationRepository.save(liveLocation);
        } else {
            liveLocation.updateLocation(latitude, longitude);
        }

        TrackPoint trackPoint = TrackPoint.builder()
                .user(user)
                .latitude(latitude)
                .longitude(longitude)
                .build();
        trackPointRepository.save(trackPoint);

        return LocationSaveResponse.builder()
                .trackPointId(trackPoint.getId())
                .latitude(trackPoint.getLatitude())
                .longitude(trackPoint.getLongitude())
                .recordedAt(trackPoint.getCreatedAt())
                .build();
    }

    public TrackPointsResponse getTrackPoints(Long requesterId, Long targetId, LocalDate date) {

        Long resolvedTargetId = (targetId != null) ? targetId : requesterId;

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        boolean isPartiallyShared = false;
        LocalDateTime sharedUntil = null;

        if (!requesterId.equals(resolvedTargetId)) {
            LocalDateTime cutoff = connectionAccessService.checkAccessAndGetCutoff(requesterId, resolvedTargetId);
            if (cutoff != null) {
                isPartiallyShared = true;
                sharedUntil = cutoff;
                if (cutoff.isBefore(endOfDay)) {
                    endOfDay = cutoff;
                }
            }
        }

        List<TrackPoint> points = trackPointRepository
                .findTrackPoints(resolvedTargetId, startOfDay, endOfDay);

        return TrackPointsResponse.from(resolvedTargetId, points, isPartiallyShared, sharedUntil);
    }

    public LiveLocationResponse getLiveLocation(Long requesterId, Long targetId) {

        Long resolvedTargetId = (targetId != null) ? targetId : requesterId;

        if (!requesterId.equals(resolvedTargetId)) {
            LocalDateTime cutoff = connectionAccessService.checkAccessAndGetCutoff(requesterId, resolvedTargetId);
            if (cutoff != null) {
                throw new CustomException(TrackErrorCode.LIVE_LOCATION_NOT_FOUND);
            }
        }

        LiveLocation liveLocation = liveLocationRepository.findByUserId(resolvedTargetId)
                .orElseThrow(() -> new CustomException(TrackErrorCode.LIVE_LOCATION_NOT_FOUND));

        return LiveLocationResponse.from(liveLocation);
    }
}