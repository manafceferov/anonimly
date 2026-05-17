package com.anonimly.controller;

import com.anonimly.config.ApiResponse;
import com.anonimly.enums.Messages;
import com.anonimly.service.LikeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/post/{postId}/like")
    public ApiResponse<Void> like(@PathVariable Long postId,
                                  @RequestParam Long userId) {
        String result = likeService.like(postId, userId);
        String message = result.equals("LIKED") ? Messages.LIKED.name() : Messages.REACTION_REMOVED.name();
        return new ApiResponse<>(true, message);
    }

    @PostMapping("/post/{postId}/dislike")
    public ApiResponse<Void> dislike(@PathVariable Long postId,
                                     @RequestParam Long userId) {
        String result = likeService.dislike(postId, userId);
        String message = result.equals("DISLIKED") ? Messages.DISLIKED.name() : Messages.REACTION_REMOVED.name();
        return new ApiResponse<>(true, message);
    }

    @GetMapping("/post/{postId}/reaction")
    public ApiResponse<String> getUserReaction(@PathVariable Long postId,
                                               @RequestParam Long userId) {
        return new ApiResponse<>(true, likeService.getUserReaction(postId, userId), Messages.SUCCESS.name());
    }
}