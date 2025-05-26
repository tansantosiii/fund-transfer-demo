package com.demo.fundtransfer.service;

import com.demo.fundtransfer.entity.Account;
import com.demo.fundtransfer.enums.CurrencyCodeEnum;
import com.demo.fundtransfer.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    private AccountService accountService;

    @BeforeEach
    void mock() {
        accountService = new AccountServiceImpl(accountRepository);
    }

    @Test
    void testFindByIdAndLock_Success() {
        Account mockAccount = new Account("Alice", BigDecimal.valueOf(1000.00), CurrencyCodeEnum.USD);
        when(accountRepository.findByIdAndLock(1L)).thenReturn(Optional.of(mockAccount));

        Account saveAccount = accountService.findByIdAndLock(1L);

        assertNotNull(saveAccount);
        assertEquals(mockAccount, saveAccount);

        verify(accountRepository, times(1)).findByIdAndLock(1L);
    }

    @Test
    void save_Success() {
        Account mockAccount = new Account("Alice", BigDecimal.valueOf(1000), CurrencyCodeEnum.USD);
        Account saveAccount = new Account(1L, "Alice", BigDecimal.valueOf(1000), CurrencyCodeEnum.USD);

        when(accountRepository.save(mockAccount)).thenReturn(saveAccount);

        Account result = accountService.save(mockAccount);
        System.out.println(result);

        assertNotNull(result);
        assertEquals(result, saveAccount);

        verify(accountRepository, times(1)).save(mockAccount);
    }

}
