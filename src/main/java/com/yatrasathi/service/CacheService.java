package com.yatrasathi.service;

import com.yatrasathi.model.CacheEntry;
import com.yatrasathi.repository.CacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final CacheRepository cacheRepository;

    public Optional<Object> get(String key) {
        return cacheRepository.findByKey(key)
                .map(CacheEntry::getData);
    }

    public void set(String key, Object data) {
        cacheRepository.deleteByKey(key);
        CacheEntry entry = CacheEntry.builder()
                .key(key)
                .data(data)
                .createdAt(Instant.now())
                .build();
        cacheRepository.save(entry);
    }
}
