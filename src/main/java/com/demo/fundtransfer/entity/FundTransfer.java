package com.demo.fundtransfer.entity;

import com.demo.fundtransfer.enums.CurrencyCodeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class FundTransfer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long sourceAccount;

    @Column(nullable = false)
    private Long targetAccount;

    @Min(0)
    @Column(nullable = false, precision = 7, scale = 2) // max transfer XX,XXX.00
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyCodeEnum currencyCode;

    @JsonIgnore
    @Column(nullable = false, updatable = false)
    private String resultCode;

}
