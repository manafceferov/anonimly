package com.anonimly.service;

import com.anonimly.dto.post.PostCreateDto;
import com.anonimly.dto.post.PostDetailResponseDto;
import com.anonimly.dto.post.PostEditDto;
import com.anonimly.dto.post.PostResponseDto;
import com.anonimly.entity.Post;
import com.anonimly.entity.User;
import com.anonimly.mapper.PostMapper;
import com.anonimly.repository.CommentRepository;
import com.anonimly.repository.PostRepository;
import com.anonimly.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostMapper postMapper;
    private final RedisTemplate<String, String> redisTemplate;

    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       CommentRepository commentRepository,
                       PostMapper postMapper,
                       RedisTemplate<String, String> redisTemplate
    ) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.postMapper = postMapper;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public PostResponseDto create(PostCreateDto dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı"));

        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setCoverImageUrl(dto.getCoverImageUrl());
        post.setPublished(dto.getPublished());
        post.setSlug(generateSlug(dto.getTitle()));
        post.setUser(user);

        PostResponseDto response = postMapper.toResponseDto(postRepository.save(post));
        enrichWithRedisData(response);
        return response;
    }

    public Page<PostResponseDto> getAll(Pageable pageable) {
        return postRepository.findAllByPublishedTrueAndDeletedFalse(pageable)
                .map(post -> {
                    PostResponseDto dto = postMapper.toResponseDto(post);
                    dto.setCommentCount(commentRepository.countByPostIdAndDeletedFalse(post.getId()));
                    enrichWithRedisData(dto);
                    return dto;
                });
    }

    public PostDetailResponseDto getBySlug(String slug) {
        Post post = postRepository.findBySlugAndDeletedFalse(slug)
                .orElseThrow(() -> new RuntimeException("Post tapılmadı"));

        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
        PostDetailResponseDto dto = postMapper.toDetailResponseDto(post);
        enrichDetailWithRedisData(dto);
        return dto;
    }

    @Transactional
    public PostResponseDto edit(Long id, PostEditDto dto, Long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post tapılmadı"));

        if (!post.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bu əməliyyat üçün icazəniz yoxdur");
        }

        if (dto.getTitle() != null) {
            post.setTitle(dto.getTitle());
            post.setSlug(generateSlug(dto.getTitle()));
        }
        if (dto.getContent() != null) post.setContent(dto.getContent());
        if (dto.getCoverImageUrl() != null) post.setCoverImageUrl(dto.getCoverImageUrl());
        if (dto.getPublished() != null) post.setPublished(dto.getPublished());

        PostResponseDto response = postMapper.toResponseDto(postRepository.save(post));
        enrichWithRedisData(response);
        return response;
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post tapılmadı"));

        if (!post.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bu əməliyyat üçün icazəniz yoxdur");
        }
        post.setDeleted(true);
        postRepository.save(post);
    }

    private void enrichWithRedisData(PostResponseDto dto) {
        String likeKey = "post:" + dto.getId() + ":likes";
        String dislikeKey = "post:" + dto.getId() + ":dislikes";
        String likeVal = redisTemplate.opsForValue().get(likeKey);
        String dislikeVal = redisTemplate.opsForValue().get(dislikeKey);
        dto.setLikeCount(likeVal != null ? Long.parseLong(likeVal) : 0L);
        dto.setDislikeCount(dislikeVal != null ? Long.parseLong(dislikeVal) : 0L);
    }

    private void enrichDetailWithRedisData(PostDetailResponseDto dto) {
        String likeKey = "post:" + dto.getId() + ":likes";
        String dislikeKey = "post:" + dto.getId() + ":dislikes";
        String likeVal = redisTemplate.opsForValue().get(likeKey);
        String dislikeVal = redisTemplate.opsForValue().get(dislikeKey);
        dto.setLikeCount(likeVal != null ? Long.parseLong(likeVal) : 0L);
        dto.setDislikeCount(dislikeVal != null ? Long.parseLong(dislikeVal) : 0L);
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