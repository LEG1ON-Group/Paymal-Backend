package com.example.paymal.services.paymentService;

import com.example.paymal.model.request.PaymentReq;
import org.springframework.http.HttpEntity;

import java.util.UUID;

public interface PaymentService {
    HttpEntity<?> create(String apiKey, PaymentReq req);

    HttpEntity<?> cancel(String apiKey, UUID id);

    HttpEntity<?> get(UUID id);
}
