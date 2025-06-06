package com.demo.fundtransfer.api.contract;

import com.demo.fundtransfer.dto.ApiResponse;
import com.demo.fundtransfer.dto.FundTransferRequest;
import com.demo.fundtransfer.entity.FundTransfer;
import com.demo.fundtransfer.enums.CurrencyCodeEnum;
import com.demo.fundtransfer.enums.ResultCodeEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class FundTransferApiJsonTest {

    @Autowired
    private JacksonTester<FundTransferRequest> testRequest;

    @Autowired
    private JacksonTester<ApiResponse<FundTransfer>> testResponse;

    @Test
    void requestSerializationTest() throws IOException {
        FundTransferRequest request = new FundTransferRequest();
        request.setSourceAccount(1L);
        request.setTargetAccount(2L);
        request.setAmount(BigDecimal.TEN);
        request.setCurrencyCode(CurrencyCodeEnum.USD.name());

        final String expectedRequest = """
            {
                "sourceAccount": 1,
                "targetAccount": 2,
                "amount": 10.00,
                "currencyCode": "USD"
            }
        """;

        assertThat(testRequest.write(request)).isStrictlyEqualToJson(expectedRequest);
        assertThat(testRequest.write(request)).hasJsonPathNumberValue("@.sourceAccount");
        assertThat(testRequest.write(request)).extractingJsonPathNumberValue("@.sourceAccount").isEqualTo(1);
        assertThat(testRequest.write(request)).hasJsonPathNumberValue("@.targetAccount");
        assertThat(testRequest.write(request)).extractingJsonPathNumberValue("@.targetAccount").isEqualTo(2);
        assertThat(testRequest.write(request)).hasJsonPathNumberValue("@.amount");
        assertThat(testRequest.write(request)).extractingJsonPathNumberValue("@.amount").isEqualTo(10);
        assertThat(testRequest.write(request)).hasJsonPathStringValue("@.currencyCode");
        assertThat(testRequest.write(request)).extractingJsonPathStringValue("@.currencyCode").isEqualTo("USD");
    }

    @Test
    void responseDeserializationTest() throws IOException {
        FundTransfer fundTransfer = new FundTransfer();
        fundTransfer.setId(1L);
        fundTransfer.setSourceAccount(1L);
        fundTransfer.setTargetAccount(2L);
        fundTransfer.setAmount(new BigDecimal("10.00"));
        fundTransfer.setCurrencyCode(CurrencyCodeEnum.USD);

        ApiResponse<FundTransfer> expectedResponse = new ApiResponse<>(fundTransfer, ResultCodeEnum.SUCCESS);

        String responseJson = """
            {
                "data": {
                    "id": 1,
                    "sourceAccount": 1,
                    "targetAccount": 2,
                    "amount": 10.00,
                    "currencyCode": "USD",
                    "resultCode": "SUCCESS"
                },
                "result": {
                    "code": "SUCCESS",
                    "msg": "Success",
                    "success": true
                }
            }
        """;

        // Assert all fields
        assertThat(testResponse.parseObject(responseJson)).usingRecursiveComparison().isEqualTo(expectedResponse);
        // Assert Data or FundTransferRequest
        assertThat(testResponse.parseObject(responseJson).getData()).usingRecursiveComparison().isEqualTo(expectedResponse.getData());
        // Assert ApiResult
        assertThat(testResponse.parseObject(responseJson).getResult()).usingRecursiveComparison().isEqualTo(expectedResponse.getResult());
        // Assert resultCode null
        assertThat(testResponse.parseObject(responseJson).getData().getResultCode()).isNull();
    }

}
