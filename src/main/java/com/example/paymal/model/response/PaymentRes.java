package com.example.paymal.model.response;

import com.example.paymal.model.enums.MerchantStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class PaymentRes {
    private UUID id;
    private MerchantRes merchant;
    private String orderId;
    private String description;
    private BigDecimal amount;
    private String status;
    private String expiresAt;
    private String paymentUrl;
    private String returnUrl;
    private BigDecimal feeAmount;
    private BigDecimal totalAmount;
}
