package com.likelion_collb.domain.user.service;

import com.likelion_collb.domain.user.dto.request.LoginRequest;
import com.likelion_collb.domain.user.dto.response.LoginResponse;
import com.likelion_collb.domain.user.entity.User;
import com.likelion_collb.domain.user.exception.UserErrorCode;
import com.likelion_collb.domain.user.repository.UserRepository;
import com.likelion_collb.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png");

    @Transactional
    public LoginResponse login(LoginRequest request) {

        String profileImageUrl = saveProfileImageIfPresent(request.getProfileImage());

        Optional<User> existingUser = userRepository.findByNickname(request.getNickname());
        boolean isNewUser = existingUser.isEmpty();

        User user = existingUser.orElseGet(() -> userRepository.save(
                User.builder()
                        .nickname(request.getNickname())
                        .profileImageUrl(profileImageUrl)
                        .build()
        ));

        return convertUserToLoginResponse(user, isNewUser);
    }

    // MultipartFile(파일) -> String(저장 경로)로 변환하는 부분
    private String saveProfileImageIfPresent(MultipartFile profileImage) {
        if (profileImage == null || profileImage.isEmpty()) {
            return null; // 선택사항이라 안 보냈으면 null 그대로
        }

        String extension = extractExtension(profileImage.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new CustomException(UserErrorCode.INVALID_PROFILE_IMAGE_TYPE);
        }

        String savedFileName = UUID.randomUUID() + "." + extension;
        File dir = new File(uploadDir).getAbsoluteFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File targetFile = new File(dir, savedFileName);

        try (InputStream inputStream = profileImage.getInputStream()) {
            Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new CustomException(UserErrorCode.PROFILE_IMAGE_SAVE_FAILED);
        }

        return "/images/" + savedFileName;
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new CustomException(UserErrorCode.INVALID_PROFILE_IMAGE_TYPE);
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    private LoginResponse convertUserToLoginResponse(User user, boolean isNewUser) {
        return LoginResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .isNewUser(isNewUser)
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}