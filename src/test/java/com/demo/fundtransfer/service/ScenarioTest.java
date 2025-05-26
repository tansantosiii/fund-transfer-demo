package com.demo.fundtransfer.service;

import com.demo.fundtransfer.dto.ApiResponse;
import com.demo.fundtransfer.dto.FundTransferRequest;
import com.demo.fundtransfer.entity.Account;
import com.demo.fundtransfer.entity.FundTransfer;
import com.demo.fundtransfer.enums.CurrencyCodeEnum;
import com.demo.fundtransfer.repository.FundTransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScenarioTest {

    @Mock
    private FundTransferRepository fundTransferRepository;

    @Mock
    private AccountService accountService;

    private FundTransferService fundTransferService;

    private static Account usdAccount = new Account(1L, "Alice", BigDecimal.valueOf(1000), CurrencyCodeEnum.USD);
    private static Account audAccount = new Account(2L, "Bob", BigDecimal.valueOf(500), CurrencyCodeEnum.AUD);

    @BeforeEach
    void mock() {
        fundTransferService = new FundTransferServiceImpl(accountService, fundTransferRepository);
    }

    @Test
    void transfer_Scenario1() {
        // Transfer USD 50 from Alice to Bob
        when(accountService.findByIdAndLock(1L)).thenReturn(usdAccount);
        when(accountService.findByIdAndLock(2L)).thenReturn(audAccount);

        FundTransferRequest request = new FundTransferRequest();
        request.setSourceAccount(usdAccount.getId());
        request.setTargetAccount(audAccount.getId());
        request.setAmount(BigDecimal.valueOf(50));
        request.setCurrencyCode(CurrencyCodeEnum.USD.name());

        ApiResponse<FundTransfer> apiResponse = fundTransferService.transfer(request);

        assertTrue(apiResponse.getResult().isSuccess());

        verify(accountService, times(2)).findByIdAndLock(anyLong());
    }

    @RepeatedTest(20)
    void transfer_Scenario2() {
        // Transfer AUD 50 from Bob to Alice recurring for 20 times
        when(accountService.findByIdAndLock(1L)).thenReturn(usdAccount);
        when(accountService.findByIdAndLock(2L)).thenReturn(audAccount);

        FundTransferRequest request = new FundTransferRequest();
        request.setSourceAccount(audAccount.getId());
        request.setTargetAccount(usdAccount.getId());
        request.setAmount(BigDecimal.valueOf(50));
        request.setCurrencyCode(CurrencyCodeEnum.AUD.name());

        ApiResponse<FundTransfer> apiResponse = fundTransferService.transfer(request);
        System.out.println("Result Code: " + apiResponse.getResult().getCode());

        assertNotNull(apiResponse.getResult());

        verify(accountService, times(2)).findByIdAndLock(anyLong());
    }

    @Execution(ExecutionMode.CONCURRENT)
    void transfer_Scenario3() {
        when(accountService.findByIdAndLock(1L)).thenReturn(usdAccount);
        when(accountService.findByIdAndLock(2L)).thenReturn(audAccount);

        // Transfer AUD 20 from Bob to Alice
        FundTransferRequest request1 = new FundTransferRequest();
        request1.setSourceAccount(audAccount.getId());
        request1.setTargetAccount(usdAccount.getId());
        request1.setAmount(BigDecimal.valueOf(20));
        request1.setCurrencyCode(CurrencyCodeEnum.AUD.name());

        // Transfer USD 40 from Alice to Bob
        FundTransferRequest request2 = new FundTransferRequest();
        request2.setSourceAccount(usdAccount.getId());
        request2.setTargetAccount(audAccount.getId());
        request2.setAmount(BigDecimal.valueOf(40));
        request2.setCurrencyCode(CurrencyCodeEnum.USD.name());

        ApiResponse<FundTransfer> apiResponse1 = fundTransferService.transfer(request1);
        ApiResponse<FundTransfer> apiResponse2 = fundTransferService.transfer(request2);

        System.out.println("#1 Result Code: " + apiResponse1.getResult().getCode());
        System.out.println("#2 Result Code: " + apiResponse2.getResult().getCode());

        assertNotNull(apiResponse1.getResult());
        assertNotNull(apiResponse2.getResult());

        verify(accountService, times(2)).findByIdAndLock(anyLong());
    }

    @Test
    void transfer_Scenario4() {
        // Transfer AUD 40 from Alice to Bob
        when(accountService.findByIdAndLock(1L)).thenReturn(usdAccount);
        when(accountService.findByIdAndLock(2L)).thenReturn(audAccount);

        FundTransferRequest request = new FundTransferRequest();
        request.setSourceAccount(usdAccount.getId());
        request.setTargetAccount(audAccount.getId());
        request.setAmount(BigDecimal.valueOf(40));
        request.setCurrencyCode(CurrencyCodeEnum.USD.name());

        ApiResponse<FundTransfer> apiResponse = fundTransferService.transfer(request);

        assertNotNull(apiResponse.getResult());

        verify(accountService, times(2)).findByIdAndLock(anyLong());
    }

}
