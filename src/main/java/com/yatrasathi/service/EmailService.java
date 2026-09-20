package com.yatrasathi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${RESEND_API_KEY}")
    private String resendApiKey;

    private final ObjectMapper objectMapper;

    public void sendOtpEmail(String email, String otp) {

        try {

            String html = """
                    <h2>YatraSathi - OTP Verification</h2>
                    
                    <p>Your YatraSathi OTP is:</p>
                    
                    <h1>%s</h1>
                    
                    <p>This OTP is valid for 10 minutes.</p>
                    
                    <p>Please do not share this OTP with anyone.</p>
                    """.formatted(otp);

            Map<String, Object> body = Map.of(
                    "from", "YatraSathi <onboarding@resend.dev>",
                    "to", new String[]{email},
                    "subject", "YatraSathi - OTP Verification",
                    "html", html
            );

            String jsonBody = objectMapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + resendApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException(
                        "Failed to send email: " + response.body()
                );
            }

        } catch (Exception e) {
            throw new RuntimeException("Error sending OTP email", e);
        }
    }
}