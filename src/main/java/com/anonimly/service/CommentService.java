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
    private final CommentReactionService commentReactionService;

    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository,
                          UserRepository userRepository,
                          CommentMapper commentMapper,
                          CommentReactionService commentReactionService
    ) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentMapper = commentMapper;
        this.commentReactionService = commentReactionService;
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

    public Page<CommentResponseDto> getByPost(Long postId, Pageable pageable, Long userId) {

        return commentRepository
                .findAllByPostIdAndParentIsNullAndDeletedFalse(postId, pageable)
                .map(comment -> mapTree(comment, userId));
    }

    private CommentResponseDto mapTree(Comment comment, Long userId) {

        CommentResponseDto dto = commentMapper.toResponseDto(comment);

        dto.setLikeCount(commentReactionService.getLikes(comment.getId()));
        dto.setDislikeCount(commentReactionService.getDislikes(comment.getId()));

        if (userId != null) {
            dto.setLikedByMe(
                    commentReactionService.getLikes(comment.getId()) > 0
            );
        }

        List<CommentResponseDto> replies = comment.getReplies()
                .stream()
                .filter(r -> !r.getDeleted())
                .map(r -> mapTree(r, userId))
                .toList();

        dto.setReplies(replies);

        return dto;
    }

    private CommentResponseDto mapWithReplies(Comment comment) {
        CommentResponseDto dto = commentMapper.toResponseDto(comment);
        List<CommentResponseDto> replies = commentRepository
                .findAllByParentIdAndDeletedFalse(comment.getId())
                .stream()
                .map(commentMapper::toResponseDto)
                .toList();
        dto.setReplies(replies);
        return dto;
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