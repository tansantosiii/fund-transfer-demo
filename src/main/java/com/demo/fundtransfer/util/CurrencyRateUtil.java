package com.demo.fundtransfer.util;

import com.demo.fundtransfer.enums.CurrencyCodeEnum;
import com.demo.fundtransfer.enums.ResultCodeEnum;
import com.demo.fundtransfer.exception.CurrencyConversionException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CurrencyRateUtil {

    private CurrencyRateUtil() {}

    private static final BigDecimal USD = BigDecimal.valueOf(0.50);

    public static BigDecimal convert(BigDecimal amount, CurrencyCodeEnum currencyCodeEnum) {
        try {
            if (currencyCodeEnum.equals(CurrencyCodeEnum.AUD)) {
                // Convert AUD to USD
                return amount.multiply(USD).setScale(2, RoundingMode.HALF_UP);
            }

            // Convert USD to AUD
            return amount.divide(USD, 2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            throw new CurrencyConversionException(ResultCodeEnum.CURRENCY_COVERT_FAILED);
        }
    }

}
