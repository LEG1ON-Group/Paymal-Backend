package com.example.paymal.services.balance;

import com.example.paymal.model.entity.Ledger;
import com.example.paymal.model.entity.Merchant;
import com.example.paymal.model.enums.ReferenceType;
import com.example.paymal.repositories.LedgerRepository;
import com.example.paymal.repositories.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final MerchantRepository merchantRepository;
    private final LedgerRepository ledgerRepository;

    @Override
    @Transactional
    public void updateBalance(Merchant merchant, BigDecimal amount, ReferenceType referenceType, Long referenceId) {
        BigDecimal balanceAfter = merchant.getBalance().add(amount);
        merchant.setBalance(balanceAfter);
        merchantRepository.save(merchant);

        Ledger ledger = Ledger.builder()
                .merchant(merchant)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .build();
        ledgerRepository.save(ledger);
    }
}
