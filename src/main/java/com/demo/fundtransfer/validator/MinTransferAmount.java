package com.demo.fundtransfer.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Constraint(validatedBy = TransferAmountValidator.class)
public @interface MinTransferAmount {

    String message() default "Invalid minimum transfer amount";
    Class<?>[] groups() default{};
    Class<? extends Payload>[] payload() default {};

}
