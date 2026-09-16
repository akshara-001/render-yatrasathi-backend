package com.yatrasathi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "galleries")
public class GalleryPhoto {

    @Id
    private String id;

    private String username;

    private String caption;

    private String dham;

    private String imageUrl;

    @Builder.Default
    private List<String> likes = new ArrayList<>();
}
