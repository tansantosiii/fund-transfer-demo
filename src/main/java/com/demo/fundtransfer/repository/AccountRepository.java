package com.demo.fundtransfer.repository;

import com.demo.fundtransfer.entity.Account;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    @QueryHints({ @QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000") })
    Optional<Account> findByIdAndLock(Long id);

}
