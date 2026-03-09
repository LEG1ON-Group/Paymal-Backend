package com.example.paymal.controllers;

import com.example.paymal.model.request.MerchantReq;
import com.example.paymal.model.request.PasswordRequest;
import com.example.paymal.services.merchantService.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @PostMapping
    public HttpEntity<?> create(@RequestBody MerchantReq req) {
        return merchantService.create(req);
    }

    @PutMapping("/{id}")
    public HttpEntity<?> update(@PathVariable UUID id, @RequestBody MerchantReq req) {
        return merchantService.update(id, req);
    }

    @GetMapping("/{id}")
    public HttpEntity<?> get(@PathVariable UUID id) {
        return merchantService.get(id);
    }

    @GetMapping
    public HttpEntity<?> getAll() {
        return merchantService.getAll();
    }

    @DeleteMapping("/{id}")
    public HttpEntity<?> delete(@PathVariable UUID id) {
        return merchantService.delete(id);
    }

    @PostMapping("/{id}/secret-key")
    public HttpEntity<?> getSecretKey(@PathVariable UUID id, @RequestBody PasswordRequest req) {
        return merchantService.getSecretKey(id, req.getPassword());
    }

    @PatchMapping("/{id}/api-key/rotate")
    public HttpEntity<?> rotateApiKey(@PathVariable UUID id) {
        return merchantService.rotateApiKey(id);
    }

    @PatchMapping("/{id}/api-secret/rotate")
    public HttpEntity<?> rotateSecretKey(@PathVariable UUID id, @RequestBody PasswordRequest req) {
        return merchantService.rotateSecretKey(id, req.getPassword());
    }
}
