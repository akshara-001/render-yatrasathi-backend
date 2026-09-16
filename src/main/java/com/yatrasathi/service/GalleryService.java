package com.yatrasathi.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.yatrasathi.dto.response.UploadResponse;
import com.yatrasathi.exception.ResourceNotFoundException;
import com.yatrasathi.model.GalleryPhoto;
import com.yatrasathi.model.User;
import com.yatrasathi.repository.GalleryRepository;
import com.yatrasathi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GalleryService {

    private final GalleryRepository galleryRepository;
    private final UserRepository userRepository;
    private final Cloudinary cloudinary;

    public UploadResponse uploadPhoto(
            MultipartFile image,
            String userId,
            String caption,
            String dham) {

        try {

            // Find the actual logged-in user using the ID from JWT
            User user = userRepository.findById(userId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("User not found"));

            Map<?, ?> uploadResult =
                    cloudinary.uploader().upload(
                            image.getBytes(),
                            ObjectUtils.emptyMap()
                    );

            String imageUrl =
                    (String) uploadResult.get("secure_url");

            GalleryPhoto photo = GalleryPhoto.builder()
                    .username(user.getName())
                    .caption(caption)
                    .dham(dham)
                    .imageUrl(imageUrl)
                    .likes(new ArrayList<>())
                    .build();

            GalleryPhoto saved =
                    galleryRepository.save(photo);

            return UploadResponse.builder()
                    .message("Uploaded")
                    .photo(saved)
                    .build();

        } catch (ResourceNotFoundException e) {
            throw e;

        } catch (Exception e) {
            log.error(
                    "Cloudinary upload failed: {}",
                    e.getMessage()
            );

            throw new RuntimeException("Upload failed");
        }
    }

    public List<GalleryPhoto> getPhotosByDham(String dhamName) {
        return galleryRepository.findByDham(dhamName);
    }

    public GalleryPhoto toggleLike(
            String id,
            String userId) {

        // Find actual user using JWT userId
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        GalleryPhoto photo =
                galleryRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Photo not found"
                                ));

        if (photo.getLikes() == null) {
            photo.setLikes(new ArrayList<>());
        }

        String username = user.getName();

        if (photo.getLikes().contains(username)) {
            photo.getLikes().remove(username);
        } else {
            photo.getLikes().add(username);
        }

        return galleryRepository.save(photo);
    }
}