package com.likelion_collb.domain.card.service;

import com.likelion_collb.domain.card.dto.request.CardCreateRequest;
import com.likelion_collb.domain.card.dto.response.CardCandidatesResponse;
import com.likelion_collb.domain.card.dto.response.CardCreateResponse;
import com.likelion_collb.domain.card.entity.Card;
import com.likelion_collb.domain.card.entity.CardPhoto;
import com.likelion_collb.domain.card.exception.CardErrorCode;
import com.likelion_collb.domain.card.repository.CardPhotoRepository;
import com.likelion_collb.domain.card.repository.CardRepository;
import com.likelion_collb.domain.photo.entity.Photo;
import com.likelion_collb.domain.photo.exception.PhotoErrorCode;
import com.likelion_collb.domain.photo.repository.PhotoRepository;
import com.likelion_collb.domain.track.entity.TrackPoint;
import com.likelion_collb.domain.track.repository.TrackPointRepository;
import com.likelion_collb.domain.user.entity.User;
import com.likelion_collb.domain.user.exception.UserErrorCode;
import com.likelion_collb.domain.user.repository.UserRepository;
import com.likelion_collb.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardService {

    private final CardRepository cardRepository;
    private final CardPhotoRepository cardPhotoRepository;
    private final TrackPointRepository trackPointRepository;
    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;

    /**
     * 1단계: 카드 후보 조회
     * - 조회 범위: user.recordStartedAt ~ until (기록 시작 이후부터 until까지)
     */
    public CardCandidatesResponse getCandidates(Long userId, LocalDateTime until) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        validateUntil(until, user);

        System.out.println("recordStartedAt = " + user.getRecordStartedAt());
        System.out.println("until = " + until);

        List<Photo> photos = photoRepository
                .findAllByUserIdAndNotDeletedBetween(userId, user.getRecordStartedAt(), until);

        System.out.println("조회된 사진 개수 = " + photos.size());
        for (Photo p : photos) {
            System.out.println(" - photoId=" + p.getId() + ", createdAt=" + p.getCreatedAt());
        }

        List<TrackPoint> trackPoints = trackPointRepository
                .findTrackPoints(userId, user.getRecordStartedAt(), until);

        return CardCandidatesResponse.of(trackPoints, photos);
    }
    /**
     * 2단계: 카드 확정 발급
     */
    @Transactional
    public CardCreateResponse createCard(CardCreateRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        validateUntil(request.getUntil(), user);

        List<Long> selectedPhotoIds = request.getSelectedPhotoIds();
        List<Photo> highlightPhotos = new ArrayList<>();

        if (selectedPhotoIds != null && !selectedPhotoIds.isEmpty()) {
            highlightPhotos = validateAndGetPhotos(
                    selectedPhotoIds, request.getUserId(), user.getRecordStartedAt(), request.getUntil());
        }

        Card card = Card.builder()
                .user(user)
                .build();
        cardRepository.save(card);

        for (Photo photo : highlightPhotos) {
            CardPhoto cardPhoto = CardPhoto.builder()
                    .card(card)
                    .photo(photo)
                    .build();
            cardPhotoRepository.save(cardPhoto);
        }

        List<TrackPoint> trackPoints = trackPointRepository
                .findTrackPoints(request.getUserId(), user.getRecordStartedAt(), request.getUntil());

        List<CardCandidatesResponse.RouteDto> route = trackPoints.stream()
                .map(CardCandidatesResponse.RouteDto::from)
                .toList();

        return CardCreateResponse.of(card, route, highlightPhotos);
    }

    /**
     * 선택된 사진 목록 검증
     * - 최대 3장 초과 여부
     * - 존재하는 사진인지
     * - 본인 사진인지
     * - recordStartedAt ~ until 범위 안에 있는지
     */
    private List<Photo> validateAndGetPhotos(List<Long> photoIds, Long userId,
                                             LocalDateTime start, LocalDateTime until) {

        if (photoIds.size() > 3) {
            throw new CustomException(CardErrorCode.INVALID_PHOTO_COUNT);
        }

        List<Photo> photos = photoRepository.findAllById(photoIds);

        if (photos.size() != photoIds.size()) {
            throw new CustomException(PhotoErrorCode.PHOTO_NOT_FOUND);
        }

        for (Photo photo : photos) {
            if (!photo.isOwnedBy(userId)) {
                throw new CustomException(CardErrorCode.NOT_YOUR_PHOTO);
            }
            if (photo.getCreatedAt().isBefore(start) || photo.getCreatedAt().isAfter(until)) {
                throw new CustomException(CardErrorCode.PHOTO_OUT_OF_RANGE);
            }
        }

        return photos;
    }

    /**
     * until 파라미터 및 기록 시작 여부 검증
     * - until이 null이면 에러
     * - until이 미래 시점이면 에러
     * - 오늘 기록을 시작하지 않았으면 에러 (recordStartedAt == null)
     * - until이 recordStartedAt보다 이전이면 에러 (범위가 무의미해짐)
     */
    private void validateUntil(LocalDateTime until, User user) {
        if (until == null) {
            throw new CustomException(CardErrorCode.INVALID_UNTIL_PARAM);
        }
        if (until.isAfter(LocalDateTime.now())) {
            throw new CustomException(CardErrorCode.FUTURE_TIME_NOT_ALLOWED);
        }
        if (user.getRecordStartedAt() == null) {
            throw new CustomException(CardErrorCode.RECORD_NOT_STARTED);
        }
        if (until.isBefore(user.getRecordStartedAt())) {
            throw new CustomException(CardErrorCode.INVALID_UNTIL_PARAM);
        }
    }
}