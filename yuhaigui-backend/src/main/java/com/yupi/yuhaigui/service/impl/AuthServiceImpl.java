package com.yupi.yuhaigui.service.impl;

import com.yupi.yuhaigui.database.mapper.UserMapper;
import com.yupi.yuhaigui.dto.LoginRequest;
import com.yupi.yuhaigui.dto.AuthResponse;
import com.yupi.yuhaigui.dto.RegisterRequest;
import com.yupi.yuhaigui.exception.AuthException;
import com.yupi.yuhaigui.security.JwtService;
import com.yupi.yuhaigui.service.AuthService;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Resource private UserMapper userMapper;
    @Resource private PasswordEncoder passwordEncoder;
    @Resource private JwtService jwtService;

    @Override
    public AuthResponse login(LoginRequest request) {
        com.yupi.yuhaigui.database.model.User user = userMapper.selectByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AuthException("INVALID_CREDENTIALS", "Invalid username or password");
        }

        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername());
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        com.yupi.yuhaigui.database.model.User existing = userMapper.selectByUsername(request.getUsername());
        if (existing != null) {
            throw new AuthException("USERNAME_EXISTS", "Username already exists");
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());
        com.yupi.yuhaigui.database.model.User user = new com.yupi.yuhaigui.database.model.User(request.getUsername(), passwordHash);
        userMapper.insert(user);

        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername());
    }
}
