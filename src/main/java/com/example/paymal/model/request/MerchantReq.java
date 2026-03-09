package com.example.paymal.model.request;

import lombok.Data;

import java.util.UUID;

@Data
public class MerchantReq {
    private String title;
    private String description;
    private String websiteUrl;
    private UUID logoId;
    private String webhookUrl;
}
