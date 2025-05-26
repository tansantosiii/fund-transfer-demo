package com.demo.fundtransfer.dto;

import com.demo.fundtransfer.enums.ResultCodeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ApiResponse<T> {

    private T data;

    private ApiResult result;

    public ApiResponse(T data, ResultCodeEnum resultCodeEnum) {
        this.data = data;
        this.result = new ApiResult(resultCodeEnum);
    }

}
