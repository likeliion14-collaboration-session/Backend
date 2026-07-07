package com.likelion_collb.domain.invite.service;

import com.likelion_collb.domain.connection.entity.Connection;
import com.likelion_collb.domain.connection.repository.ConnectionRepository;
import com.likelion_collb.domain.invite.dto.request.InviteAcceptRequest;
import com.likelion_collb.domain.invite.dto.response.InviteAcceptResponse;
import com.likelion_collb.domain.invite.dto.response.InviteCreateResponse;
import com.likelion_collb.domain.invite.entity.Invite;
import com.likelion_collb.domain.invite.entity.InviteStatus;
import com.likelion_collb.domain.invite.exception.InviteErrorCode;
import com.likelion_collb.domain.invite.repository.InviteRepository;
import com.likelion_collb.domain.share.entity.Share;
import com.likelion_collb.domain.share.repository.ShareRepository;
import com.likelion_collb.domain.user.entity.User;
import com.likelion_collb.domain.user.exception.UserErrorCode;
import com.likelion_collb.domain.user.repository.UserRepository;
import com.likelion_collb.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InviteService {

    private final InviteRepository inviteRepository;
    private final ConnectionRepository connectionRepository;
    private final ShareRepository shareRepository;
    private final UserRepository userRepository;

    private static final String CODE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private static final long EXPIRATION_MINUTES = 60;

    @Transactional
    public InviteCreateResponse createInvite(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        String code = generateUniqueCode();

        Invite invite = Invite.builder()
                .code(code)
                .status(InviteStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES))
                .user(user)
                .build();
        inviteRepository.save(invite);

        return InviteCreateResponse.from(invite);
    }

    @Transactional
    public InviteAcceptResponse acceptInvite(InviteAcceptRequest request) {

        User accepter = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        Invite invite = inviteRepository.findByCode(request.getInviteCode())
                .orElseThrow(() -> new CustomException(InviteErrorCode.INVALID_INVITE_CODE));

        if (invite.getStatus() == InviteStatus.ACCEPTED) {
            throw new CustomException(InviteErrorCode.ALREADY_USED_CODE);
        }

        if (invite.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(InviteErrorCode.EXPIRED_INVITE_CODE);
        }

        User inviter = invite.getUser();

        if (inviter.getId().equals(accepter.getId())) {
            throw new CustomException(InviteErrorCode.CANNOT_CONNECT_SELF);
        }

        boolean alreadyConnected = connectionRepository
                .findByUsers(inviter.getId(), accepter.getId())
                .isPresent();
        if (alreadyConnected) {
            throw new CustomException(InviteErrorCode.ALREADY_CONNECTED);
        }

        // Connection 생성 (작은 id를 A로 고정하는 규칙)
        Connection connection = createConnection(inviter, accepter);
        connectionRepository.save(connection);

        // Share 2행 생성 (양방향 각각 기본 true)
        createShare(connection, inviter);
        createShare(connection, accepter);

        invite.accept();

        return InviteAcceptResponse.of(connection, inviter);
    }

    private Connection createConnection(User userX, User userY) {
        User smaller = userX.getId() < userY.getId() ? userX : userY;
        User bigger = userX.getId() < userY.getId() ? userY : userX;

        return Connection.builder()
                .userA(smaller)
                .userB(bigger)
                .build();
    }

    private void createShare(Connection connection, User owner) {
        Share share = Share.builder()
                .connection(connection)
                .user(owner)
                .isShared(true)
                .build();
        shareRepository.save(share);
    }

    private String generateUniqueCode() {
        SecureRandom random = new SecureRandom();
        String code;

        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < CODE_LENGTH; i++) {
                sb.append(CODE_CHARACTERS.charAt(random.nextInt(CODE_CHARACTERS.length())));
            }
            code = sb.toString();
        } while (inviteRepository.findByCode(code).isPresent());

        return code;
    }
}