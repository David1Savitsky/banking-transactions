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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
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
            log.info("User {} is attempting to add email '{}'", userId, email);
            throw new DataAlreadyUsedException("Email is already used: " + email);
        }
        var user = userQueryService.findById(userId);
        var newEmail = emailService.createEmail(user, email);
        user.getEmailData().add(newEmail);

        userElasticService.updateUserDocument(user);
        log.info("Email '{}' added for user {}", email, userId);
    }

    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public void addPhone(final long userId, final String phone) {
        if (!validationService.isPhoneUnique(phone)) {
            log.warn("Attempt to add already used phone '{}' by user {}", phone, userId);
            throw new DataAlreadyUsedException("Phone is already used: " + phone);
        }
        var user = userQueryService.findById(userId);
        var newPhone = phoneService.createPhone(user, phone);
        user.getPhoneData().add(newPhone);

        userElasticService.updateUserDocument(user);
        log.info("Phone '{}' added for user {}", phone, userId);
    }

    @CacheEvict(value = "users", key = "#result.id")
    @Transactional
    public User updateEmail(final String oldEmail, final String newEmail) {
        if (!validationService.isEmailUnique(newEmail)) {
            log.warn("Attempt to update to already used email '{}'", newEmail);
            throw new DataAlreadyUsedException("Email is already used: " + newEmail);
        }
        emailService.updateEmail(oldEmail, newEmail);

        var user = userRepository.findByEmail(newEmail)
                .orElseThrow(() -> new DataNotFoundException("User not found with email: " + newEmail));

        userElasticService.updateUserDocument(user);
        log.info("Email updated from '{}' to '{}' for user {}", oldEmail, newEmail, user.getId());
        return user;
    }

    @CacheEvict(value = "users", key = "#result.id")
    @Transactional
    public User updatePhone(final String oldPhone, final String newPhone) {
        if (!validationService.isPhoneUnique(newPhone)) {
            log.warn("Attempt to update to already used phone '{}'", newPhone);
            throw new DataAlreadyUsedException("Phone is already used: " + newPhone);
        }
        phoneService.updatePhone(oldPhone, newPhone);

        var user = userRepository.findByPhone(newPhone)
                .orElseThrow(() -> new DataNotFoundException("User not found with phone: " + newPhone));

        userElasticService.updateUserDocument(user);
        log.info("Phone updated from '{}' to '{}' for user {}", oldPhone, newPhone, user.getId());
        return user;
    }

    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public void deleteEmail(final long userId, final String email) {
        var user = userQueryService.findById(userId);
        if (!validationService.canDeleteEmail(user) || !validationService.isEmailExist(email)) {
            log.warn("User {} tried to delete invalid email '{}'", userId, email);
            throw new ValidationException("Can not delete email: " + email);
        }
        emailService.deleteEmail(email);

        user.getEmailData().removeIf(e -> e.getEmail().equals(email));
        userElasticService.updateUserDocument(user);
        log.info("Email '{}' deleted for user {}", email, userId);
    }

    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public void deletePhone(final long userId, final String phone) {
        var user = userQueryService.findById(userId);
        if (!validationService.canDeletePhone(user) || !validationService.isPhoneExist(phone)) {
            log.warn("User {} tried to delete invalid phone '{}'", userId, phone);
            throw new ValidationException("Can not delete phone: " + phone);
        }
        phoneService.deletePhone(phone);

        user.getPhoneData().removeIf(p -> p.getPhone().equalsIgnoreCase(phone));
        userElasticService.updateUserDocument(user);
        log.info("Phone '{}' deleted for user {}", phone, userId);
    }

    public Page<User> search(final String name,
                             final String phone,
                             final String email,
                             final LocalDate dateOfBirth,
                             final PageRequest page) {
        var userDocuments = userSearchService.search(name, phone, email, dateOfBirth, page);
        log.debug("Search result: {} users found with filters: name='{}', phone='{}', email='{}', dateOfBirth={}",
                userDocuments.getTotalElements(), name, phone, email, dateOfBirth);
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
            log.warn("User {} attempted to transfer money to self", fromUserId);
            throw new ValidationException("You can't transfer money to yourself");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Invalid transfer amount {} from user {}", amount, fromUserId);
            throw new ValidationException("The transfer amount must be greater than 0");
        }

        var fromUser = userRepository.findByIdForUpdate(fromUserId)
                .orElseThrow(() -> new DataNotFoundException("Sender not found"));
        var toUser = userRepository.findByIdForUpdate(toUserId)
                .orElseThrow(() -> new DataNotFoundException("Recipient not found"));

        if (fromUser.getAccount().getBalance().compareTo(amount) < 0) {
            log.warn("User {} has insufficient funds to transfer {}", fromUserId, amount);
            throw new ValidationException("Not enough funds to transfer");
        }

        fromUser.getAccount().setBalance(fromUser.getAccount().getBalance().subtract(amount));
        toUser.getAccount().setBalance(toUser.getAccount().getBalance().add(amount));

        userRepository.save(fromUser);
        userRepository.save(toUser);
        log.info("Transferred {} from user {} to user {}", amount, fromUserId, toUserId);
    }
}
