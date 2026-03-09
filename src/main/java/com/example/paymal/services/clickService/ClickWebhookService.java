package com.example.paymal.services.clickService;

import java.util.Map;

public interface ClickWebhookService {
    Map<String, Object> prepare(Map<String, String> params);

    Map<String, Object> complete(Map<String, String> params);
}
