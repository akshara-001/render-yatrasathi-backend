package com.yatrasathi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "otp_verifications")
public class OtpVerification {

    @Id
    private String id;

    private String email;
    private String name;

    private String password;
    private String otp;

    // REGISTRATION or FORGOT_PASSWORD
    private String purpose;

    private LocalDateTime expiresAt;

    @Builder.Default
    private int attempts = 0;

    @Builder.Default
    private boolean verified = false;
}