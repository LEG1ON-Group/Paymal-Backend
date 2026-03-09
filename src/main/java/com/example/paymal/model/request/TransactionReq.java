package com.example.paymal.model.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransactionReq {
    private String provider;
}
