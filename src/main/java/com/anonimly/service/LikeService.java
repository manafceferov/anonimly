package com.anonimly.service;

import com.anonimly.entity.Like;
import com.anonimly.repository.LikeRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class LikeService {

    private final LikeRepository likeRepository;

    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    public String like(Long postId, Long userId) {
        Optional<Like> existing = likeRepository.findByPostIdAndUserId(postId, userId);

        if (existing.isPresent()) {
            if ("LIKE".equals(existing.get().getType())) {
                likeRepository.delete(existing.get());
                return "REMOVED";
            } else {
                existing.get().setType("LIKE");
                likeRepository.save(existing.get());
                return "LIKED";
            }
        }
        Like like = new Like();
        like.setPostId(postId);
        like.setUserId(userId);
        like.setType("LIKE");
        likeRepository.save(like);
        return "LIKED";
    }

    public String dislike(Long postId, Long userId) {
        Optional<Like> existing = likeRepository.findByPostIdAndUserId(postId, userId);

        if (existing.isPresent()) {
            if ("DISLIKE".equals(existing.get().getType())) {
                likeRepository.delete(existing.get());
                return "REMOVED";
            } else {
                existing.get().setType("DISLIKE");
                likeRepository.save(existing.get());
                return "DISLIKED";
            }
        }
        Like like = new Like();
        like.setPostId(postId);
        like.setUserId(userId);
        like.setType("DISLIKE");
        likeRepository.save(like);
        return "DISLIKED";
    }

    public String getUserReaction(Long postId, Long userId) {
        return likeRepository.findByPostIdAndUserId(postId, userId)
                .map(Like::getType)
                .orElse("NONE");
    }

    public long getLikeCount(Long postId) {
        return likeRepository.countByPostIdAndType(postId, "LIKE");
    }

    public long getDislikeCount(Long postId) {
        return likeRepository.countByPostIdAndType(postId, "DISLIKE");
    }
}