package com.savitsky.bankingtransactions.service;

import com.savitsky.bankingtransactions.model.User;
import com.savitsky.bankingtransactions.repository.EmailDataRepository;
import com.savitsky.bankingtransactions.repository.PhoneDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidationService {

    private final EmailDataRepository emailDataRepository;
    private final PhoneDataRepository phoneDataRepository;

    public boolean isEmailUnique(final String email) {
        return emailDataRepository.findByEmail(email).isEmpty();
    }

    public boolean isPhoneUnique(final String phone) {
        return phoneDataRepository.findByPhone(phone).isEmpty();
    }

    public boolean canDeleteEmail(final User user) {
        return user.getEmailData().size() > 1;
    }

    public boolean canDeletePhone(final User user) {
        return user.getPhoneData().size() > 1;
    }

    public boolean isEmailExist(final String email) {
        return emailDataRepository.existsByEmail(email);
    }

    public boolean isPhoneExist(final String phone) {
        return phoneDataRepository.existsByPhone(phone);
    }
}
