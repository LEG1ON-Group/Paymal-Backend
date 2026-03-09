package com.example.paymal.model.entity;

import com.example.paymal.model.enums.MerchantStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "merchant")
@EqualsAndHashCode(callSuper = true)
public class Merchant extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    private String title;

    @Enumerated(EnumType.STRING)
    private MerchantStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String websiteUrl;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "logo_id")
    private Attachment logo;

    @Column(columnDefinition = "TEXT")
    private String webhookUrl;

    @Column(columnDefinition = "TEXT")
    private String apiKey;

    @Column(columnDefinition = "TEXT")
    private String apiSecret;

    @Column(precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;
}
