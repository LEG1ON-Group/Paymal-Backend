package com.example.paymal.services.merchantService;

import com.example.paymal.model.request.MerchantReq;
import org.springframework.http.HttpEntity;

import java.util.UUID;

public interface MerchantService {
    HttpEntity<?> create(MerchantReq req);
    HttpEntity<?> update(UUID id, MerchantReq req);
    HttpEntity<?> get(UUID id);
    HttpEntity<?> getAll();
    HttpEntity<?> delete(UUID id);
    HttpEntity<?> getSecretKey(UUID id, String password);
    HttpEntity<?> rotateApiKey(UUID id);
    HttpEntity<?> rotateSecretKey(UUID id, String password);
}
