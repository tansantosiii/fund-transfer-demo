package com.demo.fundtransfer.dto;

import com.demo.fundtransfer.enums.ResultCodeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiResult {

    private String code;

    private String msg;

    private boolean success;

    public ApiResult(ResultCodeEnum resultCodeEnum) {
        setCode(resultCodeEnum.name());
        setMsg(resultCodeEnum.getMsg());
        setSuccess(resultCodeEnum == ResultCodeEnum.SUCCESS);
    }

}
