package com.anonimly.controller;

import com.anonimly.config.ApiResponse;
import com.anonimly.dto.user.UserEditDto;
import com.anonimly.dto.user.UserResponseDto;
import com.anonimly.enums.Messages;
import com.anonimly.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponseDto> getById(@PathVariable Long id) {
        return new ApiResponse<>(true, userService.getById(id), Messages.SUCCESS.name());
    }

    @GetMapping("/username/{username}")
    public ApiResponse<UserResponseDto> getByUsername(@PathVariable String username) {
        return new ApiResponse<>(true, userService.getByUsername(username), Messages.SUCCESS.name());
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponseDto> edit(@PathVariable Long id,
                                             @RequestBody UserEditDto dto) {
        return new ApiResponse<>(true, userService.edit(id, dto), Messages.UPDATED.name());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return new ApiResponse<>(true, Messages.DELETED.name());
    }
}