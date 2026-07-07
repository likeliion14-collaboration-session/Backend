package com.likelion_collb.domain.connection.service;

import com.likelion_collb.domain.connection.dto.response.ConnectionListResponse;
import com.likelion_collb.domain.connection.entity.Connection;
import com.likelion_collb.domain.connection.repository.ConnectionRepository;
import com.likelion_collb.domain.share.entity.Share;
import com.likelion_collb.domain.share.exception.ShareErrorCode;
import com.likelion_collb.domain.share.repository.ShareRepository;
import com.likelion_collb.domain.user.entity.User;
import com.likelion_collb.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final ShareRepository shareRepository;

    public ConnectionListResponse getConnections(Long userId) {

        List<Connection> connections = connectionRepository.findAllByUserId(userId);

        List<ConnectionListResponse.ConnectionDto> dtos = connections.stream()
                .map(connection -> toConnectionDto(connection, userId))
                .toList();

        return ConnectionListResponse.builder()
                .connections(dtos)
                .build();
    }

    private ConnectionListResponse.ConnectionDto toConnectionDto(Connection connection, Long myUserId) {

        User partner = connection.getPartner(myUserId);

        // "나(myUserId)"의 공유 설정을 조회 (내가 상대방에게 공유 중인지)
        Share myShare = shareRepository.findByConnectionIdAndUserId(connection.getId(), myUserId)
                .orElseThrow(() -> new CustomException(ShareErrorCode.SHARE_NOT_FOUND));

        return ConnectionListResponse.ConnectionDto.of(connection, partner, myShare.isShared());
    }
}