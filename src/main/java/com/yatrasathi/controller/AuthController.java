package com.yatrasathi.controller;

import com.yatrasathi.dto.request.LoginRequest;
import com.yatrasathi.dto.request.RegisterRequest;
import com.yatrasathi.dto.response.ApiResponse;
import com.yatrasathi.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

  /*  @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }
   */

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        System.out.println("========================================");
        System.out.println("[DEBUG] POST /api/auth/login HIT");
        System.out.println("[DEBUG] Received email: " + request.getEmail());
        System.out.println("[DEBUG] Password length: " + (request.getPassword() != null ? request.getPassword().length() : "NULL"));
        System.out.println("========================================");

        ResponseEntity<?> response = authService.login(request);

        System.out.println("[DEBUG] Login response status: " + response.getStatusCode());
        System.out.println("[DEBUG] Login response body: " + response.getBody());

        return response;
    }
}
