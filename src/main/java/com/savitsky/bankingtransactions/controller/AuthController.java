package com.savitsky.bankingtransactions.controller;

import com.savitsky.bankingtransactions.dto.LoginRequest;
import com.savitsky.bankingtransactions.dto.LoginResponse;
import com.savitsky.bankingtransactions.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody final LoginRequest loginRequest) {
        var token = authService.login(loginRequest.email(), loginRequest.password());
        return new LoginResponse(token);
    }
}
