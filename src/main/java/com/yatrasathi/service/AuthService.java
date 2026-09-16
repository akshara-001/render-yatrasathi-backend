package com.yatrasathi.service;

import com.yatrasathi.dto.request.LoginRequest;
import com.yatrasathi.dto.request.RegisterRequest;
import com.yatrasathi.dto.response.ApiResponse;
import com.yatrasathi.dto.response.LoginResponse;
import com.yatrasathi.model.User;
import com.yatrasathi.repository.UserRepository;
import com.yatrasathi.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public ResponseEntity<ApiResponse> register(RegisterRequest request) {
        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("User already exists"));
            }

            User user = User.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .build();

            userRepository.save(user);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("User registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error registering user", e.getMessage()));
        }
    }

    public ResponseEntity<?> login(LoginRequest request) {
        try {
            User user = userRepository.findByEmail(request.getEmail()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("User not found"));
            }

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Invalid credentials"));
            }

            String token = jwtUtil.generateToken(user.getId());

            LoginResponse response = LoginResponse.builder()
                    .success(true)
                    .message("Login successful")
                    .token(token)
                    .user(LoginResponse.UserData.builder()
                            .name(user.getName())
                            .email(user.getEmail())
                            .build())
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error logging in", e.getMessage()));
        }
    }
}
