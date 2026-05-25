package com.anonimly.repository;

import com.anonimly.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByPostIdAndParentIsNullAndDeletedFalse(Long postId, Pageable pageable);
    List<Comment> findAllByParentIdAndDeletedFalse(Long parentId);
    long countByPostIdAndDeletedFalse(Long postId);
    Page<Comment> findAllByUserIdAndDeletedFalse(Long userId, Pageable pageable);

}