package com.savitsky.bankingtransactions.repository;

import com.savitsky.bankingtransactions.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
