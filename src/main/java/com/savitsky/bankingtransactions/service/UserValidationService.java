package com.savitsky.bankingtransactions.service;

import com.savitsky.bankingtransactions.model.User;
import com.savitsky.bankingtransactions.repository.EmailDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserValidationService {

    private final EmailDataRepository emailDataRepository;

    public boolean isEmailUnique(final String email) {
        return emailDataRepository.findByEmail(email).isEmpty();
    }

    public boolean canDeleteEmail(final User user) {
        return user.getEmailData().size() > 1;
    }

    public boolean isEmailExist(final String email) {
        return emailDataRepository.existsByEmail(email);
    }
}
