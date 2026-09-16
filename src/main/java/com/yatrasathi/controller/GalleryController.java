package com.yatrasathi.controller;

import com.yatrasathi.dto.request.LikeRequest;
import com.yatrasathi.dto.response.UploadResponse;
import com.yatrasathi.exception.ResourceNotFoundException;
import com.yatrasathi.model.GalleryPhoto;
import com.yatrasathi.service.GalleryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/gallery")
@RequiredArgsConstructor
public class GalleryController {

    private final GalleryService galleryService;
    /*
    @PostMapping("/upload")
    public ResponseEntity<?> uploadPhoto(
            @RequestParam("image") MultipartFile image,
            @RequestParam("username") String username,
            @RequestParam("caption") String caption,
            @RequestParam("dham") String dham) {
        try {
            UploadResponse response = galleryService.uploadPhoto(image, username, caption, dham);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Upload error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Upload failed"));
        }
    }
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadPhoto(
            @RequestParam("image") MultipartFile image,
            @RequestParam("username") String username,
            @RequestParam("caption") String caption,
            @RequestParam("dham") String dham,
            Authentication authentication) {

        try {

            String userId = authentication.getName();

            UploadResponse response =
                    galleryService.uploadPhoto(
                            image,
                            userId,
                            caption,
                            dham
                    );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);

        } catch (Exception e) {
            log.error("Upload error: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Upload failed"));
        }
    }

    @GetMapping("/{dhamName}")
    public ResponseEntity<?> getPhotosByDham(@PathVariable String dhamName) {
        try {
            List<GalleryPhoto> photos = galleryService.getPhotosByDham(dhamName);
            return ResponseEntity.ok(photos);
        } catch (Exception e) {
            log.error("Error fetching gallery: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error fetching gallery"));
        }
    }
/*
    @PutMapping("/{id}/like")
    public ResponseEntity<?> toggleLike(
            @PathVariable String id,
            @RequestBody LikeRequest request) {
        try {
            GalleryPhoto photo = galleryService.toggleLike(id, request.getUsername());
            return ResponseEntity.ok(photo);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("Like error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error toggling like"));
        }
    }
    */
@PutMapping("/{id}/like")
public ResponseEntity<?> toggleLike(
        @PathVariable String id,
        @RequestBody LikeRequest request,
        Authentication authentication) {

    try {

        String userId = authentication.getName();

        GalleryPhoto photo =
                galleryService.toggleLike(id, userId);

        return ResponseEntity.ok(photo);

    } catch (ResourceNotFoundException e) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", e.getMessage()));

    } catch (Exception e) {

        log.error("Like error: {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error toggling like"));
    }
}
}
