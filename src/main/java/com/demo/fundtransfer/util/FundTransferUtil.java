package com.demo.fundtransfer.util;

import com.demo.fundtransfer.dto.FundTransferRequest;
import com.demo.fundtransfer.entity.FundTransfer;
import com.demo.fundtransfer.enums.ResultCodeEnum;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class FundTransferUtil {

    private static final BigDecimal TRANSACTION_FEE = BigDecimal.valueOf(0.01);

    private FundTransferUtil() {}

    public static FundTransfer build(FundTransferRequest request, ResultCodeEnum resultCodeEnum) {
        FundTransfer fundTransfer = new FundTransfer();
        fundTransfer.setSourceAccount(request.getSourceAccount());
        fundTransfer.setTargetAccount(request.getTargetAccount());
        fundTransfer.setAmount(request.getAmount());
        fundTransfer.setCurrencyCode(request.getCurrencyCodeEnum());
        fundTransfer.setResultCode(resultCodeEnum.name());
        return fundTransfer;
    }

    public static BigDecimal addTransactionFee(BigDecimal amount) {
        BigDecimal transFeeAmount = amount.multiply(TRANSACTION_FEE);
        return amount.add(transFeeAmount).setScale(2, RoundingMode.HALF_EVEN);
    }

}
