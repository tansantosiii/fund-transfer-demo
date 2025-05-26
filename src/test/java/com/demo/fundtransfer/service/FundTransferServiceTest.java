package com.demo.fundtransfer.service;

import com.demo.fundtransfer.dto.ApiResponse;
import com.demo.fundtransfer.dto.FundTransferRequest;
import com.demo.fundtransfer.entity.Account;
import com.demo.fundtransfer.entity.FundTransfer;
import com.demo.fundtransfer.enums.CurrencyCodeEnum;
import com.demo.fundtransfer.enums.ResultCodeEnum;
import com.demo.fundtransfer.exception.BadRequestException;
import com.demo.fundtransfer.repository.FundTransferRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FundTransferServiceTest {

    @Mock
    private FundTransferRepository fundTransferRepository;

    @Mock
    private AccountService accountService;

    private FundTransferService fundTransferService;

    private static final Account usdAccount = new Account(1L, "Alice", BigDecimal.valueOf(1000), CurrencyCodeEnum.USD);
    private static final Account audAccount = new Account(2L, "Bob", BigDecimal.valueOf(500), CurrencyCodeEnum.AUD);

    @BeforeEach
    void mock() {
        fundTransferService = new FundTransferServiceImpl(accountService, fundTransferRepository);
    }

    @Test
    void transfer_Success() {
        when(accountService.findByIdAndLock(1L)).thenReturn(usdAccount);
        when(accountService.findByIdAndLock(2L)).thenReturn(audAccount);

        FundTransferRequest request = new FundTransferRequest();
        request.setSourceAccount(1L);
        request.setTargetAccount(2L);
        request.setAmount(BigDecimal.TEN);
        request.setCurrencyCode(CurrencyCodeEnum.USD.name());

        ApiResponse<FundTransfer> apiResponse = fundTransferService.transfer(request);

        assertTrue(apiResponse.getResult().isSuccess());

        verify(accountService, times(2)).findByIdAndLock(anyLong());
    }

    @Test
    void transfer_InvalidTransferDetails() {
        FundTransferRequest request = new FundTransferRequest();
        request.setSourceAccount(1L);
        request.setTargetAccount(1L); // Same source and target account
        request.setAmount(BigDecimal.TEN);
        request.setCurrencyCode(CurrencyCodeEnum.USD.name());

        BadRequestException ex = assertThrowsExactly(BadRequestException.class, () -> fundTransferService.transfer(request));

        assertEquals(ResultCodeEnum.INVALID_TRANSFER_DETAILS, ex.getResultCodeEnum());
    }

    @Test
    void transfer_SourceAccountNotFound() {
        when(accountService.findByIdAndLock(3L)).thenThrow(new EntityNotFoundException("Not Found"));

        FundTransferRequest request = new FundTransferRequest();
        request.setSourceAccount(3L); // Invalid Source Account
        request.setTargetAccount(2L);
        request.setAmount(BigDecimal.TEN);
        request.setCurrencyCode(CurrencyCodeEnum.USD.name());

        ApiResponse<FundTransfer> apiResponse = fundTransferService.transfer(request);

        assertEquals(ResultCodeEnum.SOURCE_ACCOUNT_NOT_FOUND.name(), apiResponse.getResult().getCode());

        verify(accountService).findByIdAndLock(anyLong());
    }

    @Test
    void transfer_TargetAccountNotFound() {
        when(accountService.findByIdAndLock(1L)).thenReturn(usdAccount);
        when(accountService.findByIdAndLock(3L)).thenThrow(new EntityNotFoundException("Not Found"));

        FundTransferRequest request = new FundTransferRequest();
        request.setSourceAccount(1L);
        request.setTargetAccount(3L); // Invalid Target Account
        request.setAmount(BigDecimal.TEN);
        request.setCurrencyCode(CurrencyCodeEnum.USD.name());

        ApiResponse<FundTransfer> apiResponse = fundTransferService.transfer(request);

        assertEquals(ResultCodeEnum.TARGET_ACCOUNT_NOT_FOUND.name(), apiResponse.getResult().getCode());

        verify(accountService, times(2)).findByIdAndLock(anyLong());
    }

}
