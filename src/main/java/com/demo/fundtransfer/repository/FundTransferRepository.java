package com.demo.fundtransfer.repository;

import com.demo.fundtransfer.entity.FundTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface FundTransferRepository extends JpaRepository<FundTransfer, Long> {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    <S extends FundTransfer> S save(S entity);

}
