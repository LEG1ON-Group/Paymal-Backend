package com.example.paymal.repositories;

import com.example.paymal.model.entity.Merchant;
import com.example.paymal.model.entity.Payment;
import com.example.paymal.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findAllByMerchant(Merchant merchant);

    Optional<Payment> findByIdAndMerchant(UUID id, Merchant merchant);
}
