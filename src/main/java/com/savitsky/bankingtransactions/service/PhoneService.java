package com.savitsky.bankingtransactions.service;

import com.savitsky.bankingtransactions.exception.DataNotFoundException;
import com.savitsky.bankingtransactions.model.PhoneData;
import com.savitsky.bankingtransactions.model.User;
import com.savitsky.bankingtransactions.repository.PhoneDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PhoneService {

    private final PhoneDataRepository phoneDataRepository;

    public PhoneData createPhone(final User user, final String phone) {
        var newPhone = PhoneData.builder()
                .user(user)
                .phone(phone)
                .build();
        return phoneDataRepository.save(newPhone);
    }

    @Transactional
    public PhoneData updatePhone(final String oldPhone, final String newPhone) {
        var phoneToUpdate = phoneDataRepository.findByPhone(oldPhone)
                .orElseThrow(() -> new DataNotFoundException("Phone not found: " + oldPhone));
        phoneToUpdate.setPhone(newPhone);
        return phoneToUpdate;
    }

    public void deletePhone(final String phone) {
        phoneDataRepository.deleteByPhone(phone);
    }
}
