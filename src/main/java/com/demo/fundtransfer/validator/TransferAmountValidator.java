package com.demo.fundtransfer.validator;

import com.demo.fundtransfer.dto.FundTransferRequest;
import com.demo.fundtransfer.enums.CurrencyCodeEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class TransferAmountValidator implements ConstraintValidator<MinTransferAmount, FundTransferRequest> {

    private static final BigDecimal MIN_TRANSFER_USD = BigDecimal.TWO;
    private static final BigDecimal MIN_TRANSFER_AUD = BigDecimal.valueOf(4);


    @Override
    public boolean isValid(FundTransferRequest request, ConstraintValidatorContext context) {
        if (null == request || null == request.getAmount() || null == request.getCurrencyCodeEnum()) {
            return true;
        }

        boolean isValid =  switch (request.getCurrencyCodeEnum()) {
            case CurrencyCodeEnum.USD -> request.getAmount().compareTo(MIN_TRANSFER_USD) >= 0;
            case CurrencyCodeEnum.AUD -> request.getAmount().compareTo(MIN_TRANSFER_AUD) >= 0;
        };

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Minimum transfer amount is "
                    + request.getCurrencyCodeEnum() + " "
                    + ((request.getCurrencyCodeEnum().equals(CurrencyCodeEnum.USD)) ? MIN_TRANSFER_USD : MIN_TRANSFER_AUD))
                    .addPropertyNode("amount")
                    .addConstraintViolation();
        }

        return isValid;
    }

}
