package com.anonimly.mapper;

import com.anonimly.dto.post.PostDetailResponseDto;
import com.anonimly.dto.post.PostResponseDto;
import com.anonimly.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface PostMapper {

    @Mapping(target = "authorUsername", source = "user.username")
    @Mapping(target = "likeCount", ignore = true)
    @Mapping(target = "dislikeCount", ignore = true)
    @Mapping(target = "commentCount", ignore = true)
    @Mapping(target = "createdAt", expression = "java(post.getCreatedAt() != null ? post.getCreatedAt().toString() : null)")
    PostResponseDto toResponseDto(Post post);

    @Mapping(target = "authorUsername", source = "user.username")
    @Mapping(target = "likeCount", ignore = true)
    @Mapping(target = "dislikeCount", ignore = true)
    @Mapping(target = "createdAt", expression = "java(post.getCreatedAt() != null ? post.getCreatedAt().toString() : null)")
    @Mapping(target = "updatedAt", expression = "java(post.getUpdatedAt() != null ? post.getUpdatedAt().toString() : null)")
    PostDetailResponseDto toDetailResponseDto(Post post);
}