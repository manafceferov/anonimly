package com.anonimly.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    private final RedisTemplate<String, String> redisTemplate;

    public LikeService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String like(Long postId, Long userId) {
        String userKey = "user:" + userId + ":post:" + postId;
        String likeKey = "post:" + postId + ":likes";
        String dislikeKey = "post:" + postId + ":dislikes";
        String current = redisTemplate.opsForValue().get(userKey);

        if ("LIKE".equals(current)) {
            redisTemplate.opsForValue().decrement(likeKey);
            redisTemplate.delete(userKey);
            return "REMOVED";
        }
        if ("DISLIKE".equals(current)) {
            redisTemplate.opsForValue().decrement(dislikeKey);
        }
        redisTemplate.opsForValue().set(userKey, "LIKE");
        redisTemplate.opsForValue().increment(likeKey);
        return "LIKED";
    }

    public String dislike(Long postId, Long userId) {
        String userKey = "user:" + userId + ":post:" + postId;
        String likeKey = "post:" + postId + ":likes";
        String dislikeKey = "post:" + postId + ":dislikes";
        String current = redisTemplate.opsForValue().get(userKey);

        if ("DISLIKE".equals(current)) {
            redisTemplate.opsForValue().decrement(dislikeKey);
            redisTemplate.delete(userKey);
            return "REMOVED";
        }
        if ("LIKE".equals(current)) {
            redisTemplate.opsForValue().decrement(likeKey);
        }
        redisTemplate.opsForValue().set(userKey, "DISLIKE");
        redisTemplate.opsForValue().increment(dislikeKey);
        return "DISLIKED";
    }

    public String getUserReaction(Long postId, Long userId) {
        String userKey = "user:" + userId + ":post:" + postId;
        String reaction = redisTemplate.opsForValue().get(userKey);
        return reaction != null ? reaction : "NONE";
    }
}