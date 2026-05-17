package com.anonimly.service;

import com.anonimly.dto.auth.LoginRequestDto;
import com.anonimly.dto.auth.LoginResponseDto;
import com.anonimly.entity.User;
import com.anonimly.exception.ResourceNotFoundException;
import com.anonimly.repository.UserRepository;
import com.anonimly.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponseDto login(LoginRequestDto dto) {
        String username = dto.getUsername();
        String password = dto.getPassword();

        if (username == null || password == null) {
            throw new ResourceNotFoundException("İstifadəçi adı və ya şifrə boş ola bilməz");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi adı və ya şifrə yanlışdır"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResourceNotFoundException("İstifadəçi adı və ya şifrə yanlışdır");
        }

        String token = jwtUtil.generateToken(user.getUsername());

        LoginResponseDto response = new LoginResponseDto();
        response.setToken(token);
        response.setUsername(user.getUsername());
        response.setRole(user.getRole().name());
        return response;
    }
}