package com.demo.fundtransfer.exception;

import com.demo.fundtransfer.enums.ResultCodeEnum;
import lombok.Getter;

@Getter
public class CurrencyConversionException extends IllegalArgumentException implements ResultCode {

    private final ResultCodeEnum resultCodeEnum;

    public CurrencyConversionException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMsg());
        this.resultCodeEnum = resultCodeEnum;
    }

}
