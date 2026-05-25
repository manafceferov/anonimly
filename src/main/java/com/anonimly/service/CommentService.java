package com.anonimly.service;

import com.anonimly.dto.comment.CommentCreateDto;
import com.anonimly.dto.comment.CommentEditDto;
import com.anonimly.dto.comment.CommentResponseDto;
import com.anonimly.entity.Comment;
import com.anonimly.entity.Post;
import com.anonimly.entity.User;
import com.anonimly.mapper.CommentMapper;
import com.anonimly.repository.CommentRepository;
import com.anonimly.repository.PostRepository;
import com.anonimly.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository,
                          UserRepository userRepository,
                          CommentMapper commentMapper
    ) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentMapper = commentMapper;
    }

    @Transactional
    public CommentResponseDto create(CommentCreateDto dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı"));

        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new RuntimeException("Post tapılmadı"));

        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setUser(user);
        comment.setPost(post);

        if (dto.getParentId() != null) {
            Comment parent = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Şərh tapılmadı"));
            comment.setParent(parent);
        }

        return commentMapper.toResponseDto(commentRepository.save(comment));
    }

    public Page<CommentResponseDto> getByPost(Long postId, Pageable pageable) {
        return commentRepository.findAllByPostIdAndParentIsNullAndDeletedFalse(postId, pageable)
                .map(commentMapper::toResponseDto);
    }

    public List<CommentResponseDto> getReplies(Long parentId) {
        return commentRepository.findAllByParentIdAndDeletedFalse(parentId)
                .stream()
                .map(commentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponseDto edit(Long id, CommentEditDto dto, Long userId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Şərh tapılmadı"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bu əməliyyat üçün icazəniz yoxdur");
        }

        comment.setContent(dto.getContent());
        return commentMapper.toResponseDto(commentRepository.save(comment));
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Şərh tapılmadı"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bu əməliyyat üçün icazəniz yoxdur");
        }

        comment.setDeleted(true);
        commentRepository.save(comment);
    }

    public Page<CommentResponseDto> getByUserId(Long userId, Pageable pageable) {
        return commentRepository.findAllByUserIdAndDeletedFalse(userId, pageable)
                .map(commentMapper::toResponseDto);
    }
}