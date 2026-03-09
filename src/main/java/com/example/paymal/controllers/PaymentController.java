package com.example.paymal.controllers;

import com.example.paymal.model.entity.Transaction;
import com.example.paymal.model.request.PaymentReq;
import com.example.paymal.model.request.TransactionReq;
import com.example.paymal.services.paymentService.PaymentService;
import com.example.paymal.services.transactionService.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final TransactionService transactionService;

    @PostMapping
    public HttpEntity<?> create(@RequestHeader("x-api-key") String apiKey, @RequestBody PaymentReq req) {
        return paymentService.create(apiKey, req);
    }

    @PatchMapping("/{id}/cancel")
    public HttpEntity<?> cancel(@RequestHeader("x-api-key") String apiKey, @PathVariable UUID id) {
        return paymentService.cancel(apiKey, id);
    }

    @GetMapping("/{id}")
    public HttpEntity<?> get(@PathVariable UUID id) {
        return paymentService.get(id);
    }

    @PostMapping("/{id}/transactions")
    public HttpEntity<?> createTransaction(@RequestBody TransactionReq req, @PathVariable UUID id) {
        return transactionService.create(req, id);
    }
}
