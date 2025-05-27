package com.demo.fundtransfer.entity;

import com.demo.fundtransfer.enums.CurrencyCodeEnum;
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
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Min(0)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amountBalance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyCodeEnum currencyCode;

    public Account(String name, BigDecimal amountBalance, CurrencyCodeEnum currencyCode) {
        this.name = name;
        this.amountBalance = amountBalance;
        this.currencyCode = currencyCode;
    }

    public Account(Long id, String name, BigDecimal amountBalance, CurrencyCodeEnum currencyCode) {
        this.id = id;
        this.name = name;
        this.amountBalance = amountBalance;
        this.currencyCode = currencyCode;
    }

}
