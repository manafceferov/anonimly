package com.anonimly.service;

import com.anonimly.entity.CommentReaction;
import com.anonimly.enums.ReactionType;
import com.anonimly.repository.CommentReactionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentReactionService {

    private final CommentReactionRepository repository;

    public CommentReactionService(CommentReactionRepository repository) {
        this.repository = repository;
    }

    public String react(Long commentId, Long userId, ReactionType type) {

        Optional<CommentReaction> existing =
                repository.findByCommentIdAndUserId(commentId, userId);

        // USER artıq reaction verib
        if (existing.isPresent()) {

            CommentReaction reaction = existing.get();

            // eyni reaction click → sil (toggle off)
            if (reaction.getType() == type) {
                repository.delete(reaction);
                return "REMOVED";
            }

            // fərqli reaction → update (LIKE -> DISLIKE or DISLIKE -> LIKE)
            reaction.setType(type);
            repository.save(reaction);

            return type.name();
        }

        // ilk dəfə reaction
        repository.save(new CommentReaction(
                null,
                commentId,
                userId,
                type
        ));

        return type.name();
    }

    public long getLikes(Long commentId) {
        return repository.countByCommentIdAndType(commentId, ReactionType.LIKE);
    }

    public long getDislikes(Long commentId) {
        return repository.countByCommentIdAndType(commentId, ReactionType.DISLIKE);
    }
}