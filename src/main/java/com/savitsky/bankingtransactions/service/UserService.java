package com.savitsky.bankingtransactions.service;

import com.savitsky.bankingtransactions.exception.DataAlreadyUsedException;
import com.savitsky.bankingtransactions.exception.DataNotFoundException;
import com.savitsky.bankingtransactions.exception.ValidationException;
import com.savitsky.bankingtransactions.mapper.UserMapper;
import com.savitsky.bankingtransactions.model.User;
import com.savitsky.bankingtransactions.repository.UserRepository;
import com.savitsky.bankingtransactions.service.elastic.UserElasticService;
import com.savitsky.bankingtransactions.service.elastic.UserSearchService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ValidationService validationService;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PhoneService phoneService;
    private final UserSearchService userSearchService;
    private final UserElasticService userElasticService;
    private final UserQueryService userQueryService;

    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public void addEmail(final long userId, final String email) {
        if (!validationService.isEmailUnique(email)) {
            throw new DataAlreadyUsedException("Email is already used: " + email);
        }
        var user = userQueryService.findById(userId);
        var newEmail = emailService.createEmail(user, email);
        user.getEmailData().add(newEmail);

        userElasticService.updateUserDocument(user);
    }

    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public void addPhone(final long userId, final String phone) {
        if (!validationService.isPhoneUnique(phone)) {
            throw new DataAlreadyUsedException("Phone is already used: " + phone);
        }
        var user = userQueryService.findById(userId);
        var newPhone = phoneService.createPhone(user, phone);
        user.getPhoneData().add(newPhone);

        userElasticService.updateUserDocument(user);
    }

    @CacheEvict(value = "users", key = "#result.id")
    @Transactional
    public User updateEmail(final String oldEmail, final String newEmail) {
        if (!validationService.isEmailUnique(newEmail)) {
            throw new DataAlreadyUsedException("Email is already used: " + newEmail);
        }
        emailService.updateEmail(oldEmail, newEmail);

        var user = userRepository.findByEmail(newEmail)
                .orElseThrow(() -> new DataNotFoundException("User not found with email: " + newEmail));

        userElasticService.updateUserDocument(user);
        return user;
    }

    @CacheEvict(value = "users", key = "#result.id")
    @Transactional
    public User updatePhone(final String oldPhone, final String newPhone) {
        if (!validationService.isPhoneUnique(newPhone)) {
            throw new DataAlreadyUsedException("Phone is already used: " + newPhone);
        }
        phoneService.updatePhone(oldPhone, newPhone);

        var user = userRepository.findByPhone(newPhone)
                .orElseThrow(() -> new DataNotFoundException("User not found with phone: " + newPhone));

        userElasticService.updateUserDocument(user);
        return user;
    }

    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public void deleteEmail(final long userId, final String email) {
        var user = userQueryService.findById(userId);
        if (!validationService.canDeleteEmail(user) || !validationService.isEmailExist(email)) {
            throw new ValidationException("Can not delete email: " + email);
        }
        emailService.deleteEmail(email);

        user.getEmailData().removeIf(e -> e.getEmail().equals(email));
        userElasticService.updateUserDocument(user);
    }

    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public void deletePhone(final long userId, final String phone) {
        var user = userQueryService.findById(userId);
        if (!validationService.canDeletePhone(user) || !validationService.isPhoneExist(phone)) {
            throw new ValidationException("Can not delete phone: " + phone);
        }
        phoneService.deletePhone(phone);

        user.getPhoneData().removeIf(p -> p.getPhone().equalsIgnoreCase(phone));
        userElasticService.updateUserDocument(user);
    }

    public Page<User> search(final String name,
                             final String phone,
                             final String email,
                             final LocalDate dateOfBirth,
                             final PageRequest page) {
        var userDocuments = userSearchService.search(name, phone, email, dateOfBirth, page);
        var users = userDocuments.getContent().stream()
                .map(UserMapper::mapUserDocumentToUser)
                .toList();
        return new PageImpl<>(users, userDocuments.getPageable(), userDocuments.getTotalElements());
    }

    @Caching(evict = {
            @CacheEvict(value = "users", key = "#fromUserId"),
            @CacheEvict(value = "users", key = "#toUserId")
    })
    @Transactional
    public void transferMoney(long fromUserId, Long toUserId, BigDecimal amount) {
        if (fromUserId == toUserId) {
            throw new ValidationException("You can't transfer money to yourself");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("The transfer amount must be greater than 0");
        }

        var fromUser = userRepository.findByIdForUpdate(fromUserId)
                .orElseThrow(() -> new DataNotFoundException("Sender not found"));
        var toUser = userRepository.findByIdForUpdate(toUserId)
                .orElseThrow(() -> new DataNotFoundException("Recipient not found"));

        if (fromUser.getAccount().getBalance().compareTo(amount) < 0) {
            throw new ValidationException("Not enough funds to transfer");
        }

        fromUser.getAccount().setBalance(fromUser.getAccount().getBalance().subtract(amount));
        toUser.getAccount().setBalance(toUser.getAccount().getBalance().add(amount));

        userRepository.save(fromUser);
        userRepository.save(toUser);
    }
}
