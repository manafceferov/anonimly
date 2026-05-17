package com.anonimly.repository;

import com.anonimly.entity.Like;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LikeRepository extends MongoRepository<Like, String> {
    Optional<Like> findByPostIdAndUserId(Long postId, Long userId);
    long countByPostIdAndType(Long postId, String type);
    void deleteByPostIdAndUserId(Long postId, Long userId);
}