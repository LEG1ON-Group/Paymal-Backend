package com.example.paymal.model.entity;

import com.example.paymal.model.enums.Currency;
import com.example.paymal.model.enums.PaymentProvider;
import com.example.paymal.model.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "transaction")
@EqualsAndHashCode(callSuper = true)
public class Transaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Enumerated(EnumType.STRING)
    private PaymentProvider paymentProvider;

    @Column
    private String providerTransactionId;

    @Column
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(columnDefinition = "TEXT")
    private String transactionUrl;

    @Column(columnDefinition = "TEXT")
    private String rawResponse;

    @Column
    private String clickTransId;

    @Column
    private String clickPaydocId;

    @Column
    private String merchantPrepareId;

    @Column
    private String merchantConfirmId;
}
