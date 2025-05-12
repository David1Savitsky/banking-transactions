package com.savitsky.bankingtransactions.service;

import com.savitsky.bankingtransactions.exception.DataNotFoundException;
import com.savitsky.bankingtransactions.model.EmailData;
import com.savitsky.bankingtransactions.model.User;
import com.savitsky.bankingtransactions.repository.EmailDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailDataRepository emailDataRepository;

    public EmailData createEmail(final User user, final String email) {
        var newEmail = EmailData.builder()
                .user(user)
                .email(email)
                .build();
        return emailDataRepository.save(newEmail);
    }

    @Transactional
    public EmailData updateEmail(final String oldEmail, final String newEmail) {
        var emailToUpdate = emailDataRepository.findByEmail(oldEmail)
                .orElseThrow(() -> new DataNotFoundException("Email not found: " + oldEmail));
        emailToUpdate.setEmail(newEmail);
        return emailToUpdate;
    }

    public void deleteEmail(final String email) {
        emailDataRepository.deleteByEmail(email);
    }
}
