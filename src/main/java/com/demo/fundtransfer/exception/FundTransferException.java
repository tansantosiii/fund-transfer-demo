package com.demo.fundtransfer.exception;

import com.demo.fundtransfer.enums.ResultCodeEnum;
import lombok.Getter;

@Getter
public class FundTransferException extends RuntimeException implements ResultCode {

    private final ResultCodeEnum resultCodeEnum;

    public FundTransferException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMsg());
        this.resultCodeEnum = resultCodeEnum;
    }

}
