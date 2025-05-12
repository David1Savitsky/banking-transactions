package com.savitsky.bankingtransactions.service;

import com.savitsky.bankingtransactions.model.User;
import com.savitsky.bankingtransactions.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> findByEmail(final String email) {
        return userRepository.findByEmail(email);
    }
}
