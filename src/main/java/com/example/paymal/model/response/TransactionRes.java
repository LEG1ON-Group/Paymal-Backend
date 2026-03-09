package com.example.paymal.model.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class TransactionRes {
    private UUID id;
    private String paymentProvider;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String transactionUrl;
}
