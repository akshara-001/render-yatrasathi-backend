package com.yatrasathi.repository;

import com.yatrasathi.model.GalleryPhoto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GalleryRepository extends MongoRepository<GalleryPhoto, String> {
    List<GalleryPhoto> findByDham(String dham);
}
