package com.yatrasathi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private boolean success;
    private String message;
    private String token;
    private UserData user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserData {
        private String name;
        private String email;
    }
}
