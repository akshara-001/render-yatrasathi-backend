package com.yatrasathi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String method = request.getMethod();
        String uri = request.getRequestURI();
        System.out.println("[DEBUG][JwtFilter] >>> " + method + " " + uri);

        String authHeader = request.getHeader("Authorization");

        // No Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("[DEBUG][JwtFilter] No Bearer token present, passing through");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        System.out.println("[DEBUG][JwtFilter] Bearer token found (length=" + token.length() + ")");

        try {
            if (jwtUtil.validateToken(token)) {

                String userId =
                        jwtUtil.getUserIdFromToken(token);

                System.out.println("[DEBUG][JwtFilter] ✅ Token valid, userId=" + userId);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userId,
                                null,
                                Collections.emptyList()
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            } else {
                System.out.println("[DEBUG][JwtFilter] ❌ Token validation returned false");
            }
        } catch (Exception e) {
            // Invalid token → don't authenticate
            System.out.println("[DEBUG][JwtFilter] ❌ Token validation EXCEPTION: " + e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}