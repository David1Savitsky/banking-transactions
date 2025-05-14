package com.savitsky.bankingtransactions.service;

import com.savitsky.bankingtransactions.exception.DataNotFoundException;
import com.savitsky.bankingtransactions.model.User;
import com.savitsky.bankingtransactions.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;

    public Optional<User> findByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    @Cacheable(value = "users", key = "#id")
    public User findById(final long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + id));
    }
}
