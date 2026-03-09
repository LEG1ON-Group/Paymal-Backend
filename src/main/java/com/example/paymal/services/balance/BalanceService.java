package com.example.paymal.services.balance;

import com.example.paymal.model.entity.Merchant;
import com.example.paymal.model.enums.ReferenceType;

import java.math.BigDecimal;

public interface BalanceService {
    void updateBalance(Merchant merchant, BigDecimal amount, ReferenceType referenceType, Long referenceId);
}
