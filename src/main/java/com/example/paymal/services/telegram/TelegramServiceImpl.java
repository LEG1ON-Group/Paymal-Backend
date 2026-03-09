package com.example.paymal.services.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramServiceImpl implements TelegramService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.group.id}")
    private String groupId;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void sendMessage(String message) {
        String url = String.format("https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s&parse_mode=HTML",
                botToken, groupId, message);

        try {
            restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            log.error("Failed to send telegram message: {}", e.getMessage());
        }
    }
}
