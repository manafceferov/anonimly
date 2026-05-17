package com.anonimly.mapper;

import com.anonimly.dto.comment.CommentResponseDto;
import com.anonimly.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CommentMapper {

    @Mapping(target = "authorUsername", source = "user.username")
    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "replyCount", expression = "java(comment.getReplies().size())")
    @Mapping(target = "createdAt", expression = "java(comment.getCreatedAt() != null ? comment.getCreatedAt().toString() : null)")
    CommentResponseDto toResponseDto(Comment comment);
}