package com.savitsky.bankingtransactions.service;

import com.savitsky.bankingtransactions.model.User;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityService {

    public static long getCurrentUserId() {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getId();
    }
}
