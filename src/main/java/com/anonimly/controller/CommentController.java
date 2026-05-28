package com.anonimly.controller;

import com.anonimly.config.ApiResponse;
import com.anonimly.dto.comment.CommentCreateDto;
import com.anonimly.dto.comment.CommentEditDto;
import com.anonimly.dto.comment.CommentResponseDto;
import com.anonimly.enums.Messages;
import com.anonimly.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ApiResponse<CommentResponseDto> create(@RequestBody CommentCreateDto dto,
                                                  @RequestParam Long userId) {
        return new ApiResponse<>(true, commentService.create(dto, userId), Messages.CREATED.name());
    }

//    @GetMapping("/post/{postId}")
//    public ApiResponse<Page<CommentResponseDto>> getByPost(@PathVariable Long postId,
//                                                           Pageable pageable) {
//        return new ApiResponse<>(true, commentService.getByPost(postId, pageable), Messages.SUCCESS.name());
//    }

    @GetMapping("/{parentId}/replies")
    public ApiResponse<List<CommentResponseDto>> getReplies(@PathVariable Long parentId) {
        return new ApiResponse<>(true, commentService.getReplies(parentId), Messages.SUCCESS.name());
    }

    @PutMapping("/{id}")
    public ApiResponse<CommentResponseDto> edit(@PathVariable Long id,
                                                @RequestBody CommentEditDto dto,
                                                @RequestParam Long userId) {
        return new ApiResponse<>(true, commentService.edit(id, dto, userId), Messages.UPDATED.name());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    @RequestParam Long userId) {
        commentService.delete(id, userId);
        return new ApiResponse<>(true, Messages.DELETED.name());
    }
}