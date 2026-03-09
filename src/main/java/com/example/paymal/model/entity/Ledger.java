package com.example.paymal.model.entity;

import com.example.paymal.model.enums.ReferenceType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ledger")
@EqualsAndHashCode(callSuper = true)
public class Ledger extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Merchant merchant;

    @Enumerated(EnumType.STRING)
    private ReferenceType referenceType;

    @Column
    private Long referenceId;

    @Column
    private BigDecimal amount;

    @Column
    private BigDecimal balanceAfter;
}
