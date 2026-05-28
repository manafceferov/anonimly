package com.anonimly.repository;

import com.anonimly.entity.CommentReaction;
import com.anonimly.enums.ReactionType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CommentReactionRepository extends MongoRepository<CommentReaction, String> {

    Optional<CommentReaction> findByCommentIdAndUserId(Long commentId, Long userId);

    long countByCommentIdAndType(Long commentId, ReactionType type);
}