package com.savitsky.bankingtransactions.repository;

import com.savitsky.bankingtransactions.model.EmailData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailDataRepository extends JpaRepository<EmailData, Long> {

    Optional<EmailData> findByEmail(String email);
    void deleteByEmail(String email);
    boolean existsByEmail(String email);
}
