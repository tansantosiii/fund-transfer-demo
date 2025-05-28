package com.demo.fundtransfer.service;

import com.demo.fundtransfer.dto.FundTransferRequest;
import com.demo.fundtransfer.dto.ApiResponse;
import com.demo.fundtransfer.entity.Account;
import com.demo.fundtransfer.entity.FundTransfer;
import com.demo.fundtransfer.enums.ResultCodeEnum;
import com.demo.fundtransfer.exception.*;
import com.demo.fundtransfer.repository.FundTransferRepository;
import com.demo.fundtransfer.util.CurrencyRateUtil;
import com.demo.fundtransfer.util.FundTransferUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;

@Slf4j
@Service
public class FundTransferServiceImpl implements FundTransferService {

    private final AccountService accountService;

    private final FundTransferRepository fundTransferRepository;

    public FundTransferServiceImpl(AccountService accountService, FundTransferRepository fundTransferRepository) {
        this.accountService = accountService;
        this.fundTransferRepository = fundTransferRepository;
    }

    @Retryable(
            retryFor = {PessimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 300, multiplier = 2)
    )
    @Transactional(timeout = 5)
    @Override
    public ApiResponse<FundTransfer> transfer(FundTransferRequest request) {
        log.info("Init Fund Transfer");

        if (request.getSourceAccount().equals(request.getTargetAccount())) {
            throw new BadRequestException(ResultCodeEnum.INVALID_TRANSFER_DETAILS);
        }

        try {
            doTransfer(request);
        } catch (AccountException | FundTransferException e) {
            return apiResponseAndLog(request, e.getResultCodeEnum());
        } catch (Exception e) {
            throw new UnhandledException("Unhandled Exception: " + e.getCause());
        }
        log.info("Source and Target Amount Update Balance Success");

        return apiResponseAndLog(request, ResultCodeEnum.SUCCESS);
    }

    public void doTransfer(FundTransferRequest request) {
        log.info("doTransfer -> sourceAccountId = {}, targetAccountId = {}", request.getSourceAccount(), request.getTargetAccount());

        // Ordered locking to prevent deadlocks
        Long firstId = Math.min(request.getSourceAccount(), request.getTargetAccount());
        Long secondId = Math.max(request.getSourceAccount(), request.getTargetAccount());

        try {
            Account firstAccount = accountService.findByIdAndLock(firstId);
            Account secondAccount = accountService.findByIdAndLock(secondId);

            log.info("Lock Acquired By ID Order {}", Arrays.asList(firstId, secondId));

            boolean isFirstIdSource = firstId.equals(request.getSourceAccount());

            // Identify source and target accounts
            Account sourceAccount = isFirstIdSource ? firstAccount : secondAccount;
            Account targetAccount = isFirstIdSource ? secondAccount : firstAccount;

            log.info("sourceAccount = {}, targetAccount = {}", sourceAccount, targetAccount);

            doDebit(sourceAccount, request);
            doCredit(targetAccount, request);
        } catch (EntityNotFoundException e) {
            throw new AccountException(ResultCodeEnum.ACCOUNT_NOT_FOUND);
        }
    }

    // Add money to target account
    private void doCredit(Account targetAccount, FundTransferRequest request) {
        log.info("doCredit -> Updating target account balance... {}", targetAccount);

        try {
            BigDecimal convertedAmount = request.getAmount();

            if (!request.getCurrencyCodeEnum().equals(targetAccount.getCurrencyCode())) {
                // If currency code are different, conversion rate must be applied
                convertedAmount = CurrencyRateUtil.convert(request.getAmount(), request.getCurrencyCodeEnum());
            }

            BigDecimal newAmountBalance = targetAccount.getAmountBalance().add(convertedAmount);

            targetAccount.setAmountBalance(newAmountBalance);

            // Save Target Account
            accountService.save(targetAccount);
        } catch (CurrencyConversionException | DatabaseException e) {
            throw new FundTransferException(e.getResultCodeEnum());
        }
    }

    // Deduct money from source account
    private void doDebit(Account sourceAccount, FundTransferRequest request) {
        log.info("doDebit -> Updating source account balance... {}", sourceAccount);

        try {
            BigDecimal convertedAmount = request.getAmount();

            if (!request.getCurrencyCodeEnum().equals(sourceAccount.getCurrencyCode())) {
                // If currency code are different, conversion rate must be applied
                convertedAmount = CurrencyRateUtil.convert(request.getAmount(), request.getCurrencyCodeEnum());
            }

            // Add Transaction Fee
            BigDecimal totalAfterTransFee = FundTransferUtil.addTransactionFee(convertedAmount);

            BigDecimal newAmountBalance = sourceAccount.getAmountBalance().subtract(totalAfterTransFee);

            if (newAmountBalance.compareTo(BigDecimal.ZERO) < 0) {
                // throw if insufficient balance
                throw new FundTransferException(ResultCodeEnum.INSUFFICIENT_BALANCE);
            }

            sourceAccount.setAmountBalance(newAmountBalance);

            // Save Source Account
            accountService.save(sourceAccount);
        } catch (CurrencyConversionException | DatabaseException e) {
            throw new FundTransferException(e.getResultCodeEnum());
        }
    }

    private ApiResponse<FundTransfer> apiResponseAndLog(FundTransferRequest request, ResultCodeEnum resultCodeEnum) {
        // Log or save fund transfer
        try {
            FundTransfer responseData = fundTransferRepository.save(FundTransferUtil.build(request, resultCodeEnum));
            log.info("Save Fund Transfer Success");
            return new ApiResponse<>(responseData, resultCodeEnum);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(ResultCodeEnum.SAVE_ERROR, e.getCause());
        } catch (DataAccessException e) {
            throw new DatabaseException(ResultCodeEnum.DATABASE_ERROR, e.getCause());
        }
    }

}
