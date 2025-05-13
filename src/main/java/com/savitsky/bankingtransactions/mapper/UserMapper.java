package com.savitsky.bankingtransactions.mapper;

import com.savitsky.bankingtransactions.dto.UserDto;
import com.savitsky.bankingtransactions.model.EmailData;
import com.savitsky.bankingtransactions.model.PhoneData;
import com.savitsky.bankingtransactions.model.User;
import com.savitsky.bankingtransactions.model.elastic.UserDocument;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class UserMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static User mapUserDocumentToUser(UserDocument userDocument) {
        User user = new User();
        user.setId(userDocument.getId());
        user.setName(userDocument.getName());
        user.setDateOfBirth(userDocument.getDateOfBirth());

        var emailDataList = userDocument.getEmails().stream()
                .map(email -> new EmailData(null, user, email))
                .collect(Collectors.toList());
        user.setEmailData(emailDataList);

        var phoneDataList = userDocument.getPhones().stream()
                .map(phone -> new PhoneData(null, user, phone))
                .collect(Collectors.toList());
        user.setPhoneData(phoneDataList);

        return user;
    }

    public static UserDto mapUserToUserDto(User user) {
        var emails = user.getEmailData().stream()
                .map(EmailData::getEmail)
                .collect(Collectors.toList());

        var phones = user.getPhoneData().stream()
                .map(PhoneData::getPhone)
                .collect(Collectors.toList());

        var dateOfBirth = user.getDateOfBirth().format(DATE_FORMATTER);

        return new UserDto(user.getId(), user.getName(), emails, phones, dateOfBirth);
    }

    public static UserDocument mapUserToUserDocument(User user) {
        return UserDocument.builder()
                .id(user.getId())
                .name(user.getName())
                .emails(user.getEmailData().stream().map(EmailData::getEmail).toList())
                .phones(user.getPhoneData().stream().map(PhoneData::getPhone).toList())
                .dateOfBirth(user.getDateOfBirth())
                .build();
    }
}
