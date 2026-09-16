package com.yatrasathi.repository;

import com.yatrasathi.model.CacheEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CacheRepository extends MongoRepository<CacheEntry, String> {
    Optional<CacheEntry> findByKey(String key);
    void deleteByKey(String key);
}
