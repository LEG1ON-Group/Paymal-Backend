package com.example.paymal.model.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PaymentReq {
    private String orderId;
    private BigDecimal amount;
    private String description;
    private String returnUrl;
}
