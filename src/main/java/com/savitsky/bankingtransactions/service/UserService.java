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

    private final ValidationService validationService;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PhoneService phoneService;

    public Optional<User> findByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    public User findById(final long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + id));
    }

    @Transactional
    public void addEmail(final long userId, final String email) {
        if (!validationService.isEmailUnique(email)) {
            throw new DataAlreadyUsedException("Email is already used: " + email);
        }
        var user = findById(userId);
        var newEmail = emailService.createEmail(user, email);
        user.getEmailData().add(newEmail);
    }

    @Transactional
    public void addPhone(final long userId, final String phone) {
        if (!validationService.isPhoneUnique(phone)) {
            throw new DataAlreadyUsedException("Phone is already used: " + phone);
        }
        var user = findById(userId);
        var newPhone = phoneService.createPhone(user, phone);
        user.getPhoneData().add(newPhone);
    }

    @Transactional
    public void updateEmail(final String oldEmail, final String newEmail) {
        if (!validationService.isEmailUnique(newEmail)) {
            throw new DataAlreadyUsedException("Email is already used: " + newEmail);
        }
        emailService.updateEmail(oldEmail, newEmail);
    }

    @Transactional
    public void updatePhone(final String oldPhone, final String newPhone) {
        if (!validationService.isPhoneUnique(newPhone)) {
            throw new DataAlreadyUsedException("Phone is already used: " + newPhone);
        }
        phoneService.updatePhone(oldPhone, newPhone);
    }

    @Transactional
    public void deleteEmail(final long userId, final String email) {
        var user = findById(userId);
        if (!validationService.canDeleteEmail(user) || !validationService.isEmailExist(email)) {
            throw new ValidationException("Can not delete email: " + email);
        }
        emailService.deleteEmail(email);
    }

    @Transactional
    public void deletePhone(final long userId, final String phone) {
        var user = findById(userId);
        if (!validationService.canDeletePhone(user) || !validationService.isPhoneExist(phone)) {
            throw new ValidationException("Can not delete phone: " + phone);
        }
        phoneService.deletePhone(phone);
    }
}
