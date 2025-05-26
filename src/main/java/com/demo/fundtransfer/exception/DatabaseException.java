package com.demo.fundtransfer.exception;

import com.demo.fundtransfer.enums.ResultCodeEnum;
import lombok.Getter;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

@Getter
public class DatabaseException extends DataAccessException implements ResultCode {

    private final ResultCodeEnum resultCodeEnum;

    public DatabaseException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMsg());
        this.resultCodeEnum = resultCodeEnum;
    }

}
