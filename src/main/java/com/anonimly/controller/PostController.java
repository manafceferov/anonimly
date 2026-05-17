package com.anonimly.controller;

import com.anonimly.config.ApiResponse;
import com.anonimly.dto.post.PostCreateDto;
import com.anonimly.dto.post.PostDetailResponseDto;
import com.anonimly.dto.post.PostEditDto;
import com.anonimly.dto.post.PostResponseDto;
import com.anonimly.enums.Messages;
import com.anonimly.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ApiResponse<PostResponseDto> create(@RequestBody PostCreateDto dto,
                                               @RequestParam Long userId) {
        return new ApiResponse<>(true, postService.create(dto, userId), Messages.CREATED.name());
    }

    @GetMapping
    public ApiResponse<Page<PostResponseDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ApiResponse<>(true, postService.getAll(PageRequest.of(page, size)), Messages.SUCCESS.name());
    }

    @GetMapping("/{slug}")
    public ApiResponse<PostDetailResponseDto> getBySlug(@PathVariable String slug) {
        return new ApiResponse<>(true, postService.getBySlug(slug), Messages.SUCCESS.name());
    }

    @PutMapping("/{id}")
    public ApiResponse<PostResponseDto> edit(@PathVariable Long id,
                                             @RequestBody PostEditDto dto,
                                             @RequestParam Long userId) {
        return new ApiResponse<>(true, postService.edit(id, dto, userId), Messages.UPDATED.name());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    @RequestParam Long userId) {
        postService.delete(id, userId);
        return new ApiResponse<>(true, Messages.DELETED.name());
    }
}