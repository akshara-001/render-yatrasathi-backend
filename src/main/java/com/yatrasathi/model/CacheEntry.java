package com.yatrasathi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "caches")
public class CacheEntry {

    @Id
    private String id;

    @Indexed(unique = true)
    private String key;

    private Object data;

    @Indexed(expireAfterSeconds = 1800)
    private Instant createdAt;
}
