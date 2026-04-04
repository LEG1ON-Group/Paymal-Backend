package com.example.paymal.model.entity;

import com.example.paymal.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "payment")
@EqualsAndHashCode(callSuper = true)
public class Payment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    @Column
    private String orderId;

    @Column
    private String description;

    @Column
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column
    private LocalDateTime expiresAt;

    @Column
    private String paymentUrl;

    @Column
    private String returnUrl;

    @Column(precision = 19, scale = 4)
    private BigDecimal feeAmount;

    @Column(precision = 19, scale = 4)
    private BigDecimal totalAmount;
}
