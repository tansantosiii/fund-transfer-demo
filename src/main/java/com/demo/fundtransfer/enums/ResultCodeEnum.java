package com.demo.fundtransfer.enums;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {

    SUCCESS("Success"),
    ACCOUNT_NOT_FOUND("Account Not Found"),
    INSUFFICIENT_BALANCE("Insufficient Balance"),
    INVALID_TRANSFER_DETAILS("Invalid Transfer Details"),
    CURRENCY_COVERT_FAILED("Currency Convert Failed"),
    SAVE_ERROR("Save Error"),
    DATABASE_ERROR("Database Error");

    private final String msg;

    ResultCodeEnum(String msg) {
        this.msg = msg;
    }

}
