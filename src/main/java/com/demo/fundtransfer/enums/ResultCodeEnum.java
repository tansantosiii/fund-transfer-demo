package com.demo.fundtransfer.enums;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {

    SUCCESS("Success"),
    SOURCE_ACCOUNT_NOT_FOUND("Source Account Not Found"),
    TARGET_ACCOUNT_NOT_FOUND("Target Account Not Found"),
    SOURCE_BALANCE_UPDATE_FAILED("Source Balance Update Failed"),
    TARGET_BALANCE_UPDATE_FAILED("Target Balance Update Failed"),
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
