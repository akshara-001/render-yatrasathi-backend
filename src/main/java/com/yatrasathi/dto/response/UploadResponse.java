package com.yatrasathi.dto.response;

import com.yatrasathi.model.GalleryPhoto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponse {
    private String message;
    private GalleryPhoto photo;
}
