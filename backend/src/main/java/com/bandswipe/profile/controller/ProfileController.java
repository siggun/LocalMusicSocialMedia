package com.bandswipe.profile.controller;

import com.bandswipe.auth.entity.User;
import com.bandswipe.profile.dto.CreateProfileRequest;
import com.bandswipe.profile.dto.ProfileResponse;
import com.bandswipe.profile.dto.UpdateLocationRequest;
import com.bandswipe.profile.dto.UpdateProfileRequest;
import com.bandswipe.profile.service.FileUploadService;
import com.bandswipe.profile.service.ProfileService;
import com.bandswipe.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final FileUploadService fileUploadService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProfileResponse>> createProfile(
            @Valid @RequestBody CreateProfileRequest request,
            Authentication authentication
    ) {
        UUID userId = getCurrentUserId(authentication);
        ProfileResponse profile = profileService.createProfile(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Profile created successfully", profile));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> getMyProfile(Authentication authentication) {
        UUID userId = getCurrentUserId(authentication);
        ProfileResponse profile = profileService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", profile));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateMyProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication
    ) {
        UUID userId = getCurrentUserId(authentication);
        ProfileResponse profile = profileService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", profile));
    }

    @PostMapping(value = "/me/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProfileResponse>> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        UUID userId = getCurrentUserId(authentication);
        String photoUrl = fileUploadService.uploadPhoto(file);
        ProfileResponse profile = profileService.updatePhotoUrl(userId, photoUrl);
        return ResponseEntity.ok(ApiResponse.success("Photo uploaded successfully", profile));
    }

    @PostMapping(value = "/me/audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProfileResponse>> uploadAudio(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        UUID userId = getCurrentUserId(authentication);
        String audioUrl = fileUploadService.uploadAudio(file);
        ProfileResponse profile = profileService.updateAudioUrl(userId, audioUrl);
        return ResponseEntity.ok(ApiResponse.success("Audio intro uploaded successfully", profile));
    }

    @PutMapping("/me/location")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateLocation(
            @Valid @RequestBody UpdateLocationRequest request,
            Authentication authentication
    ) {
        UUID userId = getCurrentUserId(authentication);
        ProfileResponse profile = profileService.updateLocation(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Location updated successfully", profile));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfileByUserId(
            @PathVariable UUID userId
    ) {
        ProfileResponse profile = profileService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", profile));
    }

    private UUID getCurrentUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }
}
