package com.likelion_collb.domain.connection.service;

import com.likelion_collb.domain.connection.entity.Connection;
import com.likelion_collb.domain.connection.exception.ConnectionErrorCode;
import com.likelion_collb.domain.connection.repository.ConnectionRepository;
import com.likelion_collb.domain.share.entity.Share;
import com.likelion_collb.domain.share.exception.ShareErrorCode;
import com.likelion_collb.domain.share.repository.ShareRepository;
import com.likelion_collb.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConnectionAccessService {

    private final ConnectionRepository connectionRepository;
    private final ShareRepository shareRepository;

    /**
     * requesterId가 targetId의 데이터를 볼 수 있는지 검증하고, 컷오프 시각을 반환한다.
     * - 연동 안 되어 있으면 CONNECTION_NOT_FOUND
     * - 공유 중(is_shared=true)이면 null 반환 (컷오프 없음, 전체 조회 가능)
     * - 공유 꺼짐(is_shared=false)이면 disabledAt 반환 (그 시각 이전 데이터만 조회 가능)
     */
    public LocalDateTime checkAccessAndGetCutoff(Long requesterId, Long targetId) {

        Connection connection = connectionRepository.findByUsers(requesterId, targetId)
                .orElseThrow(() -> new CustomException(ConnectionErrorCode.CONNECTION_NOT_FOUND));

        Share targetShare = shareRepository.findByConnectionIdAndUserId(connection.getId(), targetId)
                .orElseThrow(() -> new CustomException(ShareErrorCode.SHARE_NOT_FOUND));

        if (targetShare.isShared()) {
            return null;
        }
        return targetShare.getDisabledAt();
    }
}