package com.yupi.yuhaigui.service;

import com.yupi.yuhaigui.dto.LoginRequest;
import com.yupi.yuhaigui.dto.AuthResponse;
import com.yupi.yuhaigui.dto.RegisterRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
}
