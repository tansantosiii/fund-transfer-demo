package com.demo.fundtransfer.service;

import com.demo.fundtransfer.entity.Account;

public interface AccountService {

    Account findByIdAndLock(Long id);

    Account save(Account account);

}
