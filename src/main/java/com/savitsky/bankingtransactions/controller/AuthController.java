package com.savitsky.bankingtransactions.controller;

import com.savitsky.bankingtransactions.dto.request.LoginDtoRequest;
import com.savitsky.bankingtransactions.dto.response.LoginDtoResponse;
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
    public LoginDtoResponse login(@RequestBody final LoginDtoRequest loginDtoRequest) {
        var token = authService.login(loginDtoRequest.email(), loginDtoRequest.password());
        return new LoginDtoResponse(token);
    }
}
