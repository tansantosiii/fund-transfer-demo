package com.demo.fundtransfer.service;

import com.demo.fundtransfer.dto.FundTransferRequest;
import com.demo.fundtransfer.dto.ApiResponse;
import com.demo.fundtransfer.entity.FundTransfer;

public interface FundTransferService {

    ApiResponse<FundTransfer> transfer(FundTransferRequest request);

}
