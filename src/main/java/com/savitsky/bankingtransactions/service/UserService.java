package com.savitsky.bankingtransactions.service;

import com.savitsky.bankingtransactions.exception.DataAlreadyUsedException;
import com.savitsky.bankingtransactions.exception.DataNotFoundException;
import com.savitsky.bankingtransactions.exception.ValidationException;
import com.savitsky.bankingtransactions.model.User;
import com.savitsky.bankingtransactions.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserValidationService userValidationService;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public Optional<User> findByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    public User findById(final long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + id));
    }

    @Transactional
    public void addEmail(final long userId, final String email) {
        if (!userValidationService.isEmailUnique(email)) {
            throw new DataAlreadyUsedException("Email is already used: " + email);
        }
        var user = findById(userId);
        var newEmail = emailService.createEmail(user, email);
        user.getEmailData().add(newEmail);
    }

    @Transactional
    public void updateEmail(final String oldEmail, final String newEmail) {
        if (!userValidationService.isEmailUnique(newEmail)) {
            throw new DataAlreadyUsedException("Email is already used: " + newEmail);
        }
        emailService.updateEmail(oldEmail, newEmail);
    }

    @Transactional
    public void deleteEmail(final long userId, final String email) {
        var user = findById(userId);
        if (!userValidationService.canDeleteEmail(user) || !userValidationService.isEmailExist(email)) {
            throw new ValidationException("Can not delete email: " + email);
        }
        emailService.deleteEmail(email);
    }
}
