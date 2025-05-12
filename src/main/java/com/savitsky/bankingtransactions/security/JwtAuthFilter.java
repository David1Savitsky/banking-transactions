package com.savitsky.bankingtransactions.security;

import com.savitsky.bankingtransactions.model.User;
import com.savitsky.bankingtransactions.repository.UserRepository;
import com.savitsky.bankingtransactions.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtUtils jwtUtils, UserRepository userRepository) {
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Long userId = jwtUtils.extractUserId(token);
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                var authentication = new UsernamePasswordAuthenticationToken(user, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}