package com.yatrasathi.controller;

import com.yatrasathi.dto.request.SendOtpRequest;
import com.yatrasathi.dto.request.VerifyOtpRequest;
import com.yatrasathi.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.yatrasathi.dto.request.ResetPasswordRequest;
import java.util.Map;
import com.yatrasathi.dto.request.RegisterOtpRequest;
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/register/send-otp")
    public ResponseEntity<?> sendRegistrationOtp(
            @Valid @RequestBody RegisterOtpRequest request) {

        otpService.sendRegistrationOtp(
                request.getName(),
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "OTP sent successfully"
                )
        );
    }

    @PostMapping("/register/verify-otp")
    public ResponseEntity<?> verifyRegistrationOtp(
            @Valid @RequestBody VerifyOtpRequest request) {

        otpService.verifyRegistrationOtp(
                request.getEmail(),
                request.getOtp()
        );

        return ResponseEntity.status(201).body(
                Map.of(
                        "success", true,
                        "message", "User registered successfully"
                )
        );
    }
    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<?> sendForgotPasswordOtp(
            @Valid @RequestBody SendOtpRequest request) {

        otpService.sendForgotPasswordOtp(
                request.getEmail()
        );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message",
                        "If the email is registered, an OTP has been sent."
                )
        );
    }
    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<?> verifyForgotPasswordOtp(
            @Valid @RequestBody VerifyOtpRequest request) {

        otpService.verifyForgotPasswordOtp(
                request.getEmail(),
                request.getOtp()
        );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "OTP verified successfully"
                )
        );
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        otpService.resetPassword(
                request.getEmail(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Password reset successfully"
                )
        );
    }
}