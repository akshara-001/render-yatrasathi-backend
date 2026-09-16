package com.yatrasathi.service;

import com.yatrasathi.model.OtpVerification;
import com.yatrasathi.model.User;
import com.yatrasathi.repository.OtpVerificationRepository;
import com.yatrasathi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpVerificationRepository otpRepository;
    private final EmailService emailService;

    private final SecureRandom random = new SecureRandom();

    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final int MAX_ATTEMPTS = 5;

    public void sendRegistrationOtp(
            String name,
            String email,
            String password) {

        // Don't allow OTP registration for an existing account
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User already exists");
        }
        // Invalidate previous registration OTP
        otpRepository
                .findTopByEmailAndPurposeOrderByExpiresAtDesc(
                        email,
                        "REGISTRATION")
                .ifPresent(oldOtp -> {
                    oldOtp.setVerified(true);
                    otpRepository.save(oldOtp);
                });

        // Generate secure 6-digit OTP
        String otp = String.format(
                "%06d",
                random.nextInt(1_000_000)
        );

        // Hash password before temporarily storing it
        String hashedPassword = passwordEncoder.encode(password);

        OtpVerification otpVerification = OtpVerification.builder()
                .email(email)
                .name(name)
                .password(hashedPassword)
                .otp(otp)
                .purpose("REGISTRATION")
                .expiresAt(
                        LocalDateTime.now()
                                .plusMinutes(OTP_EXPIRY_MINUTES)
                )
                .attempts(0)
                .verified(false)
                .build();

        otpRepository.save(otpVerification);

        emailService.sendOtpEmail(email, otp);
    }
    public User verifyRegistrationOtp(
            String email,
            String otp) {

        OtpVerification verification =
                otpRepository
                        .findTopByEmailAndPurposeOrderByExpiresAtDesc(
                                email,
                                "REGISTRATION")
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "OTP not found. Please request a new OTP."
                                ));

        if (verification.isVerified()) {
            throw new IllegalArgumentException(
                    "OTP has already been used."
            );
        }

        if (LocalDateTime.now().isAfter(
                verification.getExpiresAt())) {

            throw new IllegalArgumentException(
                    "OTP has expired. Please request a new OTP."
            );
        }

        if (verification.getAttempts() >= MAX_ATTEMPTS) {
            throw new IllegalArgumentException(
                    "Too many incorrect attempts. Please request a new OTP."
            );
        }

        if (!verification.getOtp().equals(otp)) {

            verification.setAttempts(
                    verification.getAttempts() + 1
            );

            otpRepository.save(verification);

            throw new IllegalArgumentException("Invalid OTP.");
        }

        // Check again in case the account was created meanwhile
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "User already exists"
            );
        }

        // Create actual user
        User user = User.builder()
                .name(verification.getName())
                .email(verification.getEmail())
                .password(verification.getPassword())
                .build();

        User savedUser = userRepository.save(user);


        verification.setVerified(true);
        otpRepository.save(verification);

        return savedUser;
    }
    public void sendForgotPasswordOtp(String email) {

        // Invalidate previous forgot-password OTP
        otpRepository
                .findTopByEmailAndPurposeOrderByExpiresAtDesc(
                        email,
                        "FORGOT_PASSWORD")
                .ifPresent(oldOtp -> {
                    oldOtp.setVerified(true);
                    otpRepository.save(oldOtp);
                });

        String otp = String.format(
                "%06d",
                random.nextInt(1_000_000)
        );

        OtpVerification verification = OtpVerification.builder()
                .email(email)
                .otp(otp)
                .purpose("FORGOT_PASSWORD")
                .expiresAt(
                        LocalDateTime.now()
                                .plusMinutes(OTP_EXPIRY_MINUTES)
                )
                .attempts(0)
                .verified(false)
                .build();

        otpRepository.save(verification);


        emailService.sendOtpEmail(email, otp);
    }
    public void verifyForgotPasswordOtp(
            String email,
            String otp) {

        OtpVerification verification =
                otpRepository
                        .findTopByEmailAndPurposeOrderByExpiresAtDesc(
                                email,
                                "FORGOT_PASSWORD")
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "OTP not found. Please request a new OTP."
                                ));

        if (verification.isVerified()) {
            throw new IllegalArgumentException(
                    "OTP has already been used."
            );
        }

        if (LocalDateTime.now().isAfter(
                verification.getExpiresAt())) {

            throw new IllegalArgumentException(
                    "OTP has expired. Please request a new OTP."
            );
        }

        if (verification.getAttempts() >= MAX_ATTEMPTS) {
            throw new IllegalArgumentException(
                    "Too many incorrect attempts. Please request a new OTP."
            );
        }

        if (!verification.getOtp().equals(otp)) {

            verification.setAttempts(
                    verification.getAttempts() + 1
            );

            otpRepository.save(verification);

            throw new IllegalArgumentException("Invalid OTP.");
        }

        verification.setVerified(true);
        otpRepository.save(verification);
    }
    public void resetPassword(
            String email,
            String newPassword) {

        OtpVerification verification =
                otpRepository
                        .findTopByEmailAndPurposeOrderByExpiresAtDesc(
                                email,
                                "FORGOT_PASSWORD")
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Please verify your OTP first."
                                ));

        if (!verification.isVerified()) {
            throw new IllegalArgumentException(
                    "Please verify your OTP first."
            );
        }

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Unable to reset password."
                        ));

        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        userRepository.save(user);

        // Prevent this verification from being reused
        verification.setVerified(false);
        otpRepository.save(verification);
    }


}