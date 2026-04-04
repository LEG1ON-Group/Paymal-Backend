package com.example.paymal.services.telegram;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor

public class TelegramServiceImpl implements TelegramService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.group.id}")
    private String groupId;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void sendMessage(String message) {
        String url = String.format("https://api.telegram.org/bot%s/sendMessage", botToken);
        
        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("chat_id", groupId);
        body.put("text", message);
        body.put("parse_mode", "HTML");


        try {
            org.springframework.http.ResponseEntity<String> response = restTemplate.postForEntity(url, body, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {

            } else {

            }
        } catch (org.springframework.web.client.HttpClientErrorException e) {

        } catch (Exception e) {

        }
    }
}
