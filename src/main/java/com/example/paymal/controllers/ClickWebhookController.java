package com.example.paymal.controllers;

import com.example.paymal.services.clickService.ClickWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks/click")
@RequiredArgsConstructor
@Slf4j
public class ClickWebhookController {

    private final ClickWebhookService clickWebhookService;

    @PostMapping("/prepare")
    public ResponseEntity<?> prepare(@RequestParam Map<String, String> params) {
        log.info("Click Prepare request received: {}", params);
        Map<String, Object> response = clickWebhookService.prepare(params);
        log.info("Click Prepare response sent: {}", response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/complete")
    public ResponseEntity<?> complete(@RequestParam Map<String, String> params) {
        log.info("Click Complete request received: {}", params);
        Map<String, Object> response = clickWebhookService.complete(params);
        log.info("Click Complete response sent: {}", response);
        return ResponseEntity.ok(response);
    }
}
