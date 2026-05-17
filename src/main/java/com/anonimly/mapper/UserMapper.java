package com.anonimly.mapper;

import com.anonimly.dto.user.UserResponseDto;
import com.anonimly.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {

    @Mapping(target = "role", expression = "java(user.getRole().name())")
    UserResponseDto toResponseDto(User user);
}