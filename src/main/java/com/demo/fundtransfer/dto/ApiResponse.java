package com.demo.fundtransfer.dto;

import com.demo.fundtransfer.enums.ResultCodeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiResponse<T> {

    private T data;

    private ApiResult result;

    public ApiResponse(T data, ResultCodeEnum resultCodeEnum) {
        this.data = data;
        this.result = new ApiResult(resultCodeEnum);
    }

}
