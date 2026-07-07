package com.likelion_collb.domain.share.service;

import com.likelion_collb.domain.share.dto.request.SharingToggleRequest;
import com.likelion_collb.domain.share.dto.response.SharingToggleResponse;
import com.likelion_collb.domain.share.entity.Share;
import com.likelion_collb.domain.share.exception.ShareErrorCode;
import com.likelion_collb.domain.share.repository.ShareRepository;
import com.likelion_collb.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShareService {

    private final ShareRepository shareRepository;


    //공유 토글
    @Transactional
    public SharingToggleResponse toggleSharing(Long connectionId, SharingToggleRequest request) {

        Share share = shareRepository.findByConnectionIdAndUserId(connectionId, request.getUserId())
                .orElseThrow(() -> new CustomException(ShareErrorCode.SHARE_NOT_FOUND));

        if (Boolean.TRUE.equals(request.getEnabled())) {
            share.enable();
        } else {
            share.disable();
        }

        return SharingToggleResponse.from(share);
    }
}