package com.savitsky.bankingtransactions.controller;

import com.savitsky.bankingtransactions.dto.UserDto;
import com.savitsky.bankingtransactions.dto.request.EmailDtoRequest;
import com.savitsky.bankingtransactions.dto.request.PhoneDataRequest;
import com.savitsky.bankingtransactions.dto.request.TransferDtoRequest;
import com.savitsky.bankingtransactions.dto.request.UpdateEmailDtoRequest;
import com.savitsky.bankingtransactions.dto.request.UpdatePhoneDtoRequest;
import com.savitsky.bankingtransactions.mapper.UserMapper;
import com.savitsky.bankingtransactions.service.SecurityService;
import com.savitsky.bankingtransactions.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/search")
    public Page<UserDto> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) LocalDate dateOfBirth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var userPage = userService.search(name, phone, email, dateOfBirth, PageRequest.of(page, size));
        var users = userPage.getContent().stream()
                .map(UserMapper::mapUserToUserDto)
                .toList();
        return new PageImpl<>(users, userPage.getPageable(), userPage.getTotalElements());
    }

    @PostMapping("/transfer")
    public void transferMoney(@RequestBody final TransferDtoRequest request) {
        userService.transferMoney(SecurityService.getCurrentUserId(), request.toUserId(), request.amount());
    }

    @PostMapping("/emails")
    public void addEmail(@RequestBody final EmailDtoRequest emailDtoRequest) {
        userService.addEmail(SecurityService.getCurrentUserId(), emailDtoRequest.email());
    }

    @PostMapping("/phones")
    public void addPhone(@RequestBody final PhoneDataRequest phoneDataRequest) {
        userService.addPhone(SecurityService.getCurrentUserId(), phoneDataRequest.phone());
    }

    @PatchMapping("/emails")
    public void updateEmail(@RequestBody final UpdateEmailDtoRequest updateEmailDtoRequest) {
        userService.updateEmail(updateEmailDtoRequest.oldEmail(), updateEmailDtoRequest.newEmail());
    }

    @PatchMapping("/phones")
    public void updatePhone(@RequestBody final UpdatePhoneDtoRequest updatePhoneDtoRequest) {
        userService.updatePhone(updatePhoneDtoRequest.oldPhone(), updatePhoneDtoRequest.newPhone());
    }

    @DeleteMapping("/emails")
    public void deleteEmail(@RequestBody final EmailDtoRequest emailDtoRequest) {
        userService.deleteEmail(SecurityService.getCurrentUserId(), emailDtoRequest.email());
    }

    @DeleteMapping("/phones")
    public void deletePhone(@RequestBody final PhoneDataRequest phoneDataRequest) {
        userService.deletePhone(SecurityService.getCurrentUserId(), phoneDataRequest.phone());
    }
}
