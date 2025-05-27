package com.demo.fundtransfer.dto;

import com.demo.fundtransfer.enums.CurrencyCodeEnum;
import com.demo.fundtransfer.validator.MinTransferAmount;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@MinTransferAmount
public class FundTransferRequest {

    @NotNull(message = "sourceAccount must not be null")
    private Long sourceAccount;

    @NotNull(message = "targetAccount must not be null")
    private Long targetAccount;

    @Digits(integer = 7, fraction = 2, message = "max decimal of 2")
    @NotNull(message = "amount must not be null")
    private BigDecimal amount;

    @NotBlank(message = "currencyCode must not be blank")
    private String currencyCode;

    public CurrencyCodeEnum getCurrencyCodeEnum() {
        return CurrencyCodeEnum.valueOf(currencyCode.toUpperCase());
    }

}
