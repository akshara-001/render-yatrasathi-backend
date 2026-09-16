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
            System.out.println("[DEBUG][AuthService] login() called for email: " + request.getEmail());

            User user = userRepository.findByEmail(request.getEmail()).orElse(null);

            if (user == null) {
                System.out.println("[DEBUG][AuthService] ❌ User NOT FOUND in database for email: " + request.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("User not found"));
            }

            System.out.println("[DEBUG][AuthService] ✅ User FOUND: id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail());
            System.out.println("[DEBUG][AuthService] Stored password hash: " + user.getPassword().substring(0, Math.min(20, user.getPassword().length())) + "...");

            boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());
            System.out.println("[DEBUG][AuthService] Password matches: " + passwordMatches);

            if (!passwordMatches) {
                System.out.println("[DEBUG][AuthService] ❌ Password mismatch for email: " + request.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Invalid credentials"));
            }

            System.out.println("[DEBUG][AuthService] Generating JWT token for userId: " + user.getId());
            String token = jwtUtil.generateToken(user.getId());
            System.out.println("[DEBUG][AuthService] ✅ JWT token generated (length=" + token.length() + ")");

            LoginResponse response = LoginResponse.builder()
                    .success(true)
                    .message("Login successful")
                    .token(token)
                    .user(LoginResponse.UserData.builder()
                            .name(user.getName())
                            .email(user.getEmail())
                            .build())
                    .build();

            System.out.println("[DEBUG][AuthService] ✅ Login SUCCESS for: " + user.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("[DEBUG][AuthService] ❌ EXCEPTION during login: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error logging in", e.getMessage()));
        }
    }
}
