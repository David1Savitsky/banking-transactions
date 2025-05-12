package com.savitsky.bankingtransactions.controller;

import com.savitsky.bankingtransactions.dto.request.EmailDtoRequest;
import com.savitsky.bankingtransactions.dto.request.UpdateEmailDtoRequest;
import com.savitsky.bankingtransactions.service.SecurityService;
import com.savitsky.bankingtransactions.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/emails")
    public void addEmail(@RequestBody final EmailDtoRequest emailDtoRequest) {
        userService.addEmail(SecurityService.getCurrentUserId(), emailDtoRequest.email());
    }

    @PatchMapping("/emails")
    public void updateEmail(@RequestBody final UpdateEmailDtoRequest updateEmailDtoRequest) {
        userService.updateEmail(updateEmailDtoRequest.oldEmail(), updateEmailDtoRequest.newEmail());
    }

    @DeleteMapping("/emails")
    public void deleteEmail(@RequestBody final EmailDtoRequest emailDtoRequest) {
        userService.deleteEmail(SecurityService.getCurrentUserId(), emailDtoRequest.email());
    }
}
