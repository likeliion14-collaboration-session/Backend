package com.likelion_collb.domain.photo.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.likelion_collb.domain.photo.dto.request.PhotoUpdateRequest;
import com.likelion_collb.domain.photo.dto.request.PhotoUploadRequest;
import com.likelion_collb.domain.photo.dto.response.*;
import com.likelion_collb.domain.photo.entity.Photo;
import com.likelion_collb.domain.photo.exception.PhotoErrorCode;
import com.likelion_collb.domain.photo.repository.PhotoRepository;
import com.likelion_collb.domain.user.entity.User;
import com.likelion_collb.domain.user.exception.UserErrorCode;
import com.likelion_collb.domain.user.repository.UserRepository;
import com.likelion_collb.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;

    // ./upload
    @Value("${file.upload-dir}")
    private String uploadDir;

    // 허용되는 확장자
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png");

    @Transactional
    public PhotoUploadResponse upload(PhotoUploadRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        // 오늘 기록 시작했는지 검증
        if (user.getRecordStartedAt() == null
                || !user.getRecordStartedAt().toLocalDate().equals(LocalDate.now())) {
            throw new CustomException(PhotoErrorCode.RECORD_NOT_STARTED);
        }

        MultipartFile file = request.getImage();

        // 파일이 비어있는지 확인
        if (file == null || file.isEmpty()) {
            throw new CustomException(PhotoErrorCode.EMPTY_FILE);
        }

        // 지원하는 확장자인지 확인
        String extension = extractExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new CustomException(PhotoErrorCode.INVALID_FILE_TYPE);
        }

        // EXIF 추출
        Double latitude = null;
        Double longitude = null;
        LocalDateTime takenAt = null;

        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file.getInputStream());

            GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            if (gpsDirectory != null) {
                GeoLocation geoLocation = gpsDirectory.getGeoLocation();
                if (geoLocation != null && !geoLocation.isZero()) {
                    latitude = geoLocation.getLatitude();
                    longitude = geoLocation.getLongitude();
                }
            }

            ExifSubIFDDirectory exifDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (exifDirectory != null) {
                Date date = exifDirectory.getDateOriginal();
                if (date != null) {
                    takenAt = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                }
            }
        } catch (ImageProcessingException | IOException e) {
            // EXIF 파싱 실패해도 업로드는 계속 진행
        }

        // 파일 저장
        String savedFileName = UUID.randomUUID() + "." + extension;
        File dir = new File(uploadDir).getAbsoluteFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File targetFile = new File(dir, savedFileName);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new CustomException(PhotoErrorCode.FILE_SAVE_FAILED);
        }

        String imageUrl = "/images/" + savedFileName;
        Photo photo = Photo.builder()
                .user(user)
                .imgUrl(imageUrl)
                .latitude(latitude)
                .longitude(longitude)
                .takenAt(takenAt)
                .comment(request.getComment())
                .pinIcon(request.getPinIcon())
                .build();
        photoRepository.save(photo);

        return PhotoUploadResponse.from(photo);
    }

    public PhotoPinsResponse getPins(Long requesterId, Long targetId) {

        Long resolvedTargetId = (targetId != null) ? targetId : requesterId;

        // TODO: Connection/Share 도메인 완성되면 여기서 접근 제어 검증 추가

        List<Photo> photos = photoRepository.findPhotoPins(resolvedTargetId);

        return PhotoPinsResponse.from(photos);
    }

    public PhotoDetailResponse getDetail(Long photoId) {

        Photo photo = photoRepository.findByIdAndNotDeleted(photoId)
                .orElseThrow(() -> new CustomException(PhotoErrorCode.PHOTO_NOT_FOUND));

        return PhotoDetailResponse.from(photo);
    }

    @Transactional
    public PhotoUpdateResponse update(Long photoId, PhotoUpdateRequest request) {

        Photo photo = photoRepository.findByIdAndNotDeleted(photoId)
                .orElseThrow(() -> new CustomException(PhotoErrorCode.PHOTO_NOT_FOUND));

        if (!photo.isOwnedBy(request.getUserId())) {
            throw new CustomException(PhotoErrorCode.FORBIDDEN);
        }

        photo.updateContent(request.getComment(), request.getPinIcon());

        return PhotoUpdateResponse.from(photo);
    }

    @Transactional
    public PhotoDeleteResponse delete(Long photoId, Long userId) {

        Photo photo = photoRepository.findByIdAndNotDeleted(photoId)
                .orElseThrow(() -> new CustomException(PhotoErrorCode.PHOTO_NOT_FOUND));

        if (!photo.isOwnedBy(userId)) {
            throw new CustomException(PhotoErrorCode.FORBIDDEN);
        }

        photo.softDelete();

        return PhotoDeleteResponse.from(photo);
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new CustomException(PhotoErrorCode.INVALID_FILE_TYPE);
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}