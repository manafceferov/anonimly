package com.anonimly.service;

import com.anonimly.dto.post.PostCreateDto;
import com.anonimly.dto.post.PostDetailResponseDto;
import com.anonimly.dto.post.PostEditDto;
import com.anonimly.dto.post.PostResponseDto;
import com.anonimly.entity.Post;
import com.anonimly.entity.User;
import com.anonimly.exception.ForbiddenException;
import com.anonimly.exception.ResourceNotFoundException;
import com.anonimly.mapper.PostMapper;
import com.anonimly.repository.CommentRepository;
import com.anonimly.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final PostMapper postMapper;
    private final LikeService likeService;

    public PostService(PostRepository postRepository,
                       UserService userService,
                       CommentRepository commentRepository,
                       PostMapper postMapper,
                       LikeService likeService
    ) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.commentRepository = commentRepository;
        this.postMapper = postMapper;
        this.likeService = likeService;
    }

    @Transactional
    public PostResponseDto create(PostCreateDto dto, Long userId) {
        User user = userService.findById(userId);

        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setCoverImageUrl(dto.getCoverImageUrl());
        post.setPublished(dto.getPublished());
        post.setSlug(generateSlug(dto.getTitle()));
        post.setUser(user);

        PostResponseDto response = postMapper.toResponseDto(postRepository.save(post));
        enrichWithLikes(response);
        return response;
    }

    public Page<PostResponseDto> getAll(Pageable pageable) {
        return postRepository.findAllByPublishedTrueAndDeletedFalse(pageable)
                .map(post -> {
                    PostResponseDto dto = postMapper.toResponseDto(post);
                    dto.setCommentCount(commentRepository.countByPostIdAndDeletedFalse(post.getId()));
                    enrichWithLikes(dto);
                    return dto;
                });
    }

    @Transactional
    public PostDetailResponseDto getBySlug(String slug) {
        Post post = postRepository.findBySlugAndDeletedFalse(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Post tapılmadı"));
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
        PostDetailResponseDto dto = postMapper.toDetailResponseDto(post);
        enrichDetailWithLikes(dto);
        return dto;
    }

    @Transactional
    public PostResponseDto edit(Long id, PostEditDto dto, Long userId) {
        Post post = findById(id);
        if (!post.getUser().getId().equals(userId))
            throw new ForbiddenException("Bu əməliyyat üçün icazəniz yoxdur");

        if (dto.getTitle() != null) {
            post.setTitle(dto.getTitle());
            post.setSlug(generateSlug(dto.getTitle()));
        }
        if (dto.getContent() != null) post.setContent(dto.getContent());
        if (dto.getCoverImageUrl() != null) post.setCoverImageUrl(dto.getCoverImageUrl());
        if (dto.getPublished() != null) post.setPublished(dto.getPublished());

        PostResponseDto response = postMapper.toResponseDto(postRepository.save(post));
        enrichWithLikes(response);
        return response;
    }

    public Page<PostResponseDto> getByUserId(Long userId, Pageable pageable) {
        return postRepository.findAllByUserIdAndDeletedFalse(userId, pageable)
                .map(post -> {
                    PostResponseDto dto = postMapper.toResponseDto(post);
                    dto.setCommentCount(commentRepository.countByPostIdAndDeletedFalse(post.getId()));
                    enrichWithLikes(dto);
                    return dto;
                });
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Post post = findById(id);
        if (!post.getUser().getId().equals(userId))
            throw new ForbiddenException("Bu əməliyyat üçün icazəniz yoxdur");
        post.setDeleted(true);
        postRepository.save(post);
    }

    public Post findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post tapılmadı"));
    }

    private void enrichWithLikes(PostResponseDto dto) {
        dto.setLikeCount(likeService.getLikeCount(dto.getId()));
        dto.setDislikeCount(likeService.getDislikeCount(dto.getId()));
    }

    private void enrichDetailWithLikes(PostDetailResponseDto dto) {
        dto.setLikeCount(likeService.getLikeCount(dto.getId()));
        dto.setDislikeCount(likeService.getDislikeCount(dto.getId()));
    }

    private String generateSlug(String title) {
        String normalized = Normalizer.normalize(title, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized)
                .replaceAll("")
                .toLowerCase(Locale.ENGLISH)
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-") + "-" + System.currentTimeMillis();
    }
}