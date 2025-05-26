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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
public class FundTransferServiceImpl implements FundTransferService {

    private final AccountService accountService;

    private final FundTransferRepository fundTransferRepository;

    public FundTransferServiceImpl(AccountService accountService, FundTransferRepository fundTransferRepository) {
        this.accountService = accountService;
        this.fundTransferRepository = fundTransferRepository;
    }

    @Transactional(timeout = 5)
    @Override
    public ApiResponse<FundTransfer> transfer(FundTransferRequest request) {
        log.info("Init Fund Transfer");

        if (request.getSourceAccount().equals(request.getTargetAccount())) {
            throw new BadRequestException(ResultCodeEnum.INVALID_TRANSFER_DETAILS);
        }

        try {
            doDebit(request);
            doCredit(request);
        } catch (AccountException | CurrencyConversionException | FundTransferException | DatabaseException e) {
            return apiResponseAndLog(request, e.getResultCodeEnum());
        } catch (Exception e) {
            throw new UnhandledException("Unhandled Exception: " + e.getCause());
        }
        log.info("Source and Target Amount Update Balance Success");

        return apiResponseAndLog(request, ResultCodeEnum.SUCCESS);
    }

    // Add money to target account
    private synchronized void doCredit(FundTransferRequest request) throws Exception {
        log.info("doCredit -> Updating target account balance...");

        try {
            Account targetAccount = accountService.findByIdAndLock(request.getTargetAccount());
            log.info("Target Account Find By ID and Lock Success");

            BigDecimal convertedAmount = request.getAmount();

            if (!request.getCurrencyCodeEnum().equals(targetAccount.getCurrencyCode())) {
                // If currency code are different, conversion rate must be applied
                convertedAmount = CurrencyRateUtil.convert(request.getAmount(), request.getCurrencyCodeEnum());
            }

            BigDecimal newAmountBalance = targetAccount.getAmountBalance().add(convertedAmount);

            targetAccount.setAmountBalance(newAmountBalance);

            // Save Target Account
            accountService.save(targetAccount);
        } catch (EntityNotFoundException e) {
            throw new AccountException(ResultCodeEnum.TARGET_ACCOUNT_NOT_FOUND);
        } catch (CurrencyConversionException e) {
            throw new CurrencyConversionException(e.getResultCodeEnum());
        } catch (DatabaseException e) {
            throw new FundTransferException(ResultCodeEnum.TARGET_BALANCE_UPDATE_FAILED);
        }
    }

    // Deduct money from source account
    private synchronized void doDebit(FundTransferRequest request) throws Exception {
        log.info("doDebit -> Updating source account balance...");

        try {
            Account sourceAccount = accountService.findByIdAndLock(request.getSourceAccount());
            log.info("Source Account Find By ID and Lock Success");

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
        } catch (EntityNotFoundException e) {
            throw new AccountException(ResultCodeEnum.SOURCE_ACCOUNT_NOT_FOUND);
        } catch (CurrencyConversionException e) {
            throw new CurrencyConversionException(e.getResultCodeEnum());
        } catch (DatabaseException e) {
            throw new FundTransferException(ResultCodeEnum.SOURCE_BALANCE_UPDATE_FAILED);
        }
    }

    private ApiResponse<FundTransfer> apiResponseAndLog(FundTransferRequest request, ResultCodeEnum resultCodeEnum) {
        // Log or save fund transfer
        try {
            FundTransfer responseData = fundTransferRepository.save(FundTransferUtil.build(request, resultCodeEnum));
            log.info("Save Fund Transfer Success");
            return new ApiResponse<>(responseData, resultCodeEnum);
        } catch (DataAccessException e) {
            throw new DatabaseException(ResultCodeEnum.SAVE_ERROR);
        }
    }

}
