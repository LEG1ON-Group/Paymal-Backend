package com.example.paymal.services.transactionService;

import com.example.paymal.model.request.TransactionReq;
import org.springframework.http.HttpEntity;

import java.util.UUID;

public interface TransactionService {
    HttpEntity<?> create(TransactionReq req, UUID paymentId);

    HttpEntity<?> cancel(String apiKey, UUID id);

    HttpEntity<?> get(UUID id);
}
