package com.yatrasathi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String email, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("YatraSathi - OTP Verification");
        message.setText(
                "Your YatraSathi OTP is: " + otp +
                        "\n\nThis OTP is valid for 10 minutes." +
                        "\n\nPlease do not share this OTP with anyone."
        );

        mailSender.send(message);
    }
}