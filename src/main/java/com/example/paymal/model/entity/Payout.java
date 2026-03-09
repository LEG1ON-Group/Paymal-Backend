package com.example.paymal.model.entity;

import com.example.paymal.model.enums.Currency;
import com.example.paymal.model.enums.PayoutDestination;
import com.example.paymal.model.enums.PayoutStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "payouts")
@EqualsAndHashCode(callSuper = true)
public class Payout extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Merchant merchant;

    @Column
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    private PayoutStatus status;

    @Enumerated(EnumType.STRING)
    private PayoutDestination destination;
}
