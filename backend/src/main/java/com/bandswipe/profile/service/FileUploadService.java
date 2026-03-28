package com.bandswipe.profile.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final Cloudinary cloudinary;

    private static final long MAX_PHOTO_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final long MAX_AUDIO_SIZE = 5 * 1024 * 1024;  // 5 MB

    public String uploadPhoto(MultipartFile file) {
        validateFileNotEmpty(file);
        validateFileSize(file, MAX_PHOTO_SIZE, "Photo");

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "bandswipe/photos",
                    "transformation", "w_800,h_800,c_limit,q_auto"
            ));
            return (String) result.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload photo to Cloudinary", e);
        }
    }

    public String uploadAudio(MultipartFile file) {
        validateFileNotEmpty(file);
        validateFileSize(file, MAX_AUDIO_SIZE, "Audio");

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("audio/")) {
            throw new IllegalArgumentException("File must be an audio file");
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "bandswipe/audio",
                    "resource_type", "video"
            ));
            return (String) result.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload audio to Cloudinary", e);
        }
    }

    private void validateFileNotEmpty(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }
    }

    private void validateFileSize(MultipartFile file, long maxSize, String fileType) {
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException(
                    fileType + " file size must not exceed " + (maxSize / (1024 * 1024)) + " MB"
            );
        }
    }
}
