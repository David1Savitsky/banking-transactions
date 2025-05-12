package com.savitsky.bankingtransactions.repository;

import com.savitsky.bankingtransactions.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u " +
            "JOIN u.emailData ed " +
            "WHERE ed.email = :email")
    Optional<User> findByEmail(String email);
}
