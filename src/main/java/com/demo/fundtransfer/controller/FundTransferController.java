package com.demo.fundtransfer.controller;

import com.demo.fundtransfer.dto.ApiResponse;
import com.demo.fundtransfer.dto.FundTransferRequest;
import com.demo.fundtransfer.entity.FundTransfer;
import com.demo.fundtransfer.service.FundTransferService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class FundTransferController {

    private final FundTransferService fundTransferService;

    public FundTransferController(FundTransferService fundTransferService) {
        this.fundTransferService = fundTransferService;
    }

    @PostMapping("/fund-transfer")
    public ResponseEntity<ApiResponse<FundTransfer>> transfer(@Valid @RequestBody FundTransferRequest request) {
        ApiResponse<FundTransfer> apiResponse = fundTransferService.transfer(request);

        if (apiResponse == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(apiResponse);
    }

}
