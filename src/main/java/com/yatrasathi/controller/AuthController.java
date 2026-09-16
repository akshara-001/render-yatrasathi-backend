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
        return authService.login(request);
    }
}
