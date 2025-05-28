package com.demo.fundtransfer.util;

import com.demo.fundtransfer.enums.CurrencyCodeEnum;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CurrencyRateUtilTest {

    @Test
    void testConvert() {
        BigDecimal usdAmount1 = CurrencyRateUtil.convert(BigDecimal.valueOf(2.75), CurrencyCodeEnum.USD);
        assertEquals(new BigDecimal("5.50"), usdAmount1);

        BigDecimal usdAmount2 = CurrencyRateUtil.convert(BigDecimal.valueOf(2.015), CurrencyCodeEnum.USD);
        assertEquals(new BigDecimal("4.03"), usdAmount2);

        BigDecimal audAmount1 = CurrencyRateUtil.convert(BigDecimal.valueOf(4.266), CurrencyCodeEnum.AUD);
        assertEquals(new BigDecimal("2.13"), audAmount1);

        BigDecimal audAmount2 = CurrencyRateUtil.convert(BigDecimal.valueOf(5.1234), CurrencyCodeEnum.AUD);
        assertEquals(new BigDecimal("2.56"), audAmount2);
    }

}
