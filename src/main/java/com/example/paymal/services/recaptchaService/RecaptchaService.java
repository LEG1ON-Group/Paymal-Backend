package com.example.paymal.services.recaptchaService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecaptchaService {

    @Value("${recaptcha.secret-key}")
    private String secretKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public void verify(String token) {
        String url = "https://www.google.com/recaptcha/api/siteverify?secret=" + secretKey + "&response=" + token;

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);
            Map<String, Object> body = response.getBody();

            if (body == null || !Boolean.TRUE.equals(body.get("success"))) {
                throw new RuntimeException("Recaptcha verification failed");
            }

            Double score = (Double) body.get("score");
            if (score == null || score < 0.5) {
                throw new com.example.paymal.exceptions.CustomException(
                        "Harakatlaringiz robotga o'xshaydi",
                        HttpStatus.BAD_REQUEST
                );
            }

        } catch (com.example.paymal.exceptions.CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new com.example.paymal.exceptions.CustomException(
                    "Recaptcha verification error",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
