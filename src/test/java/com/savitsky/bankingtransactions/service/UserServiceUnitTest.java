package com.savitsky.bankingtransactions.service;

import com.savitsky.bankingtransactions.exception.DataNotFoundException;
import com.savitsky.bankingtransactions.exception.ValidationException;
import com.savitsky.bankingtransactions.model.Account;
import com.savitsky.bankingtransactions.model.User;
import com.savitsky.bankingtransactions.repository.UserRepository;
import com.savitsky.bankingtransactions.service.elastic.UserElasticService;
import com.savitsky.bankingtransactions.service.elastic.UserSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceUnitTest {

    private ValidationService validationService;
    private UserRepository userRepository;
    private EmailService emailService;
    private PhoneService phoneService;
    private UserSearchService userSearchService;
    private UserElasticService userElasticService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        validationService = mock(ValidationService.class);
        userRepository = mock(UserRepository.class);
        emailService = mock(EmailService.class);
        phoneService = mock(PhoneService.class);
        userSearchService = mock(UserSearchService.class);
        userElasticService = mock(UserElasticService.class);

        userService = new UserService(
                validationService,
                userRepository,
                emailService,
                phoneService,
                userSearchService,
                userElasticService
        );
    }

    @Test
    void shouldTransferMoneySuccessfully() {
        var fromUserId = 1L;
        var toUserId = 2L;
        var amount = new BigDecimal("50.00");

        var fromUser = new User();
        fromUser.setId(fromUserId);
        fromUser.setAccount(new Account(1L, fromUser, new BigDecimal("100.00"), new BigDecimal("100.00")));

        var toUser = new User();
        toUser.setId(toUserId);
        toUser.setAccount(new Account(2L, toUser, new BigDecimal("30.00"), new BigDecimal("20.00")));

        when(userRepository.findByIdForUpdate(fromUserId)).thenReturn(Optional.of(fromUser));
        when(userRepository.findByIdForUpdate(toUserId)).thenReturn(Optional.of(toUser));

        userService.transferMoney(fromUserId, toUserId, amount);

        assertEquals(new BigDecimal("50.00"), fromUser.getAccount().getBalance());
        assertEquals(new BigDecimal("70.00"), toUser.getAccount().getBalance());

        verify(userRepository).save(fromUser);
        verify(userRepository).save(toUser);
    }

    @Test
    void shouldThrowWhenTransferToSelf() {
        var ex = assertThrows(ValidationException.class,
                () -> userService.transferMoney(1L, 1L, new BigDecimal("10.00")));
        assertEquals("You can't transfer money to yourself", ex.getMessage());
    }

    @Test
    void shouldThrowWhenAmountIsZero() {
        var ex = assertThrows(ValidationException.class,
                () -> userService.transferMoney(1L, 2L, BigDecimal.ZERO));
        assertEquals("The transfer amount must be greater than 0", ex.getMessage());
    }

    @Test
    void shouldThrowWhenSenderNotFound() {
        when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.empty());

        var ex = assertThrows(DataNotFoundException.class,
                () -> userService.transferMoney(1L, 2L, new BigDecimal("10.00")));
        assertEquals("Sender not found", ex.getMessage());
    }

    @Test
    void shouldThrowWhenRecipientNotFound() {
        User sender = new User();
        sender.setAccount(new Account(1L, sender, new BigDecimal("100.00"), new BigDecimal("100.00")));
        when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findByIdForUpdate(2L)).thenReturn(Optional.empty());

        var ex = assertThrows(DataNotFoundException.class,
                () -> userService.transferMoney(1L, 2L, new BigDecimal("10.00")));
        assertEquals("Recipient not found", ex.getMessage());
    }

    @Test
    void shouldThrowWhenInsufficientFunds() {
        User sender = new User();
        sender.setAccount(new Account(1L, sender, new BigDecimal("10.00"), new BigDecimal("5.00")));
        User recipient = new User();
        recipient.setAccount(new Account(2L, recipient, new BigDecimal("20.00"), new BigDecimal("20.00")));

        when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(recipient));

        var ex = assertThrows(ValidationException.class,
                () -> userService.transferMoney(1L, 2L, new BigDecimal("10.00")));
        assertEquals("Not enough funds to transfer", ex.getMessage());
    }
}