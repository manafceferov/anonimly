package com.anonimly.service;

import com.anonimly.dto.user.UserEditDto;
import com.anonimly.dto.user.UserRegisterDto;
import com.anonimly.dto.user.UserResponseDto;
import com.anonimly.entity.User;
import com.anonimly.enums.Role;
import com.anonimly.exception.AlreadyExistsException;
import com.anonimly.exception.ResourceNotFoundException;
import com.anonimly.mapper.UserMapper;
import com.anonimly.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDto register(UserRegisterDto dto) {
        if (userRepository.existsByEmail(dto.getEmail()))
            throw new AlreadyExistsException("Bu email artıq mövcuddur");
        if (userRepository.existsByUsername(dto.getUsername()))
            throw new AlreadyExistsException("Bu username artıq mövcuddur");

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.USER);
        return userMapper.toResponseDto(userRepository.save(user));
    }

    public UserResponseDto getById(Long id) {
        return userMapper.toResponseDto(findById(id));
    }

    public UserResponseDto getByUsername(String username) {
        return userMapper.toResponseDto(findByUsername(username));
    }

    @Transactional
    public UserResponseDto edit(Long id, UserEditDto dto) {
        User user = findById(id);
        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getBio() != null) user.setBio(dto.getBio());
        if (dto.getAvatarUrl() != null) user.setAvatarUrl(dto.getAvatarUrl());
        return userMapper.toResponseDto(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        User user = findById(id);
        user.setDeleted(true);
        userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı"));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı"));
    }
}