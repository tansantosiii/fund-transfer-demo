package com.demo.fundtransfer.exception;

import com.demo.fundtransfer.enums.ResultCodeEnum;
import lombok.Getter;
import org.springframework.dao.DataAccessException;

@Getter
public class DatabaseException extends DataAccessException implements ResultCode {

    private final ResultCodeEnum resultCodeEnum;

    public DatabaseException(ResultCodeEnum resultCodeEnum, Throwable cause) {
        super(resultCodeEnum.getMsg(), cause);
        this.resultCodeEnum = resultCodeEnum;
    }

}
