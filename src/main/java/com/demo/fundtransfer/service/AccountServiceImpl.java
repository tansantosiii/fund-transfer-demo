package com.demo.fundtransfer.service;

import com.demo.fundtransfer.entity.Account;
import com.demo.fundtransfer.enums.ResultCodeEnum;
import com.demo.fundtransfer.exception.DatabaseException;
import com.demo.fundtransfer.exception.UnhandledException;
import com.demo.fundtransfer.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account findByIdAndLock(Long id) {
        log.info("AccountService => findByIdAndLock = " + id);

        try {
            Optional<Account> optionalAccount = accountRepository.findByIdAndLock(id);
            return optionalAccount.orElseThrow(() -> new EntityNotFoundException("findByIdAndLock = " + id + " Not Found"));
        } catch (DataAccessException e) {
            throw new DatabaseException(ResultCodeEnum.DATABASE_ERROR);
        }
    }

    @Override
    public Account save(Account account) {
        log.info("AccountService => save = " + account.toString());

        try {
            return accountRepository.save(account);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(ResultCodeEnum.SAVE_ERROR);
        } catch (DataAccessException e) {
            throw new DatabaseException(ResultCodeEnum.DATABASE_ERROR);
        }
    }

}
