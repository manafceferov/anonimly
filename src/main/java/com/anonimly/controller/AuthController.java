package com.anonimly.controller;

import com.anonimly.config.ApiResponse;
import com.anonimly.dto.auth.LoginRequestDto;
import com.anonimly.dto.auth.LoginResponseDto;
import com.anonimly.dto.user.UserRegisterDto;
import com.anonimly.dto.user.UserResponseDto;
import com.anonimly.enums.Messages;
import com.anonimly.service.AuthService;
import com.anonimly.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService,
                          UserService userService
    ) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ApiResponse<UserResponseDto> register(@RequestBody UserRegisterDto dto) {
        return new ApiResponse<>(true, userService.register(dto), Messages.CREATED.name());
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponseDto> login(@RequestBody LoginRequestDto dto) {
        return new ApiResponse<>(true, authService.login(dto), Messages.SUCCESS.name());
    }
}