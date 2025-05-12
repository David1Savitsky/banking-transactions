package com.savitsky.bankingtransactions.repository;

import com.savitsky.bankingtransactions.model.PhoneData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhoneDataRepository extends JpaRepository<PhoneData, Long> {

    Optional<PhoneData> findByPhone(String phone);
    boolean existsByPhone(String phone);
    void deleteByPhone(String phone);
}
