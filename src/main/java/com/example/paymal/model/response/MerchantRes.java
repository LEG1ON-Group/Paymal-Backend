package com.example.paymal.model.response;

import com.example.paymal.model.enums.MerchantStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MerchantRes {
    private UUID id;
    private String title;
    private String description;
    private String websiteUrl;
    private String imageUrl;
    private String webhookUrl;
    private String apiKey;
    private MerchantStatus status;
}
