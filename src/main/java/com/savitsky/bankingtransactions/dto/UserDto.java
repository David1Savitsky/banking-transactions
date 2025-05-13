package com.savitsky.bankingtransactions.dto;

import java.util.List;

public record UserDto (Long id, String name, List<String> email, List<String> phone, String dateOfBirth) {
}
