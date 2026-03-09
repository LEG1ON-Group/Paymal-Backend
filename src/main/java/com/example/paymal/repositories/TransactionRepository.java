package com.example.paymal.repositories;

import com.example.paymal.model.entity.Payment;
import com.example.paymal.model.entity.Transaction;
import com.example.paymal.model.enums.PaymentProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findAllByPayment(Payment payment);

    Optional<Transaction> findByIdAndPayment(UUID id, Payment payment);

    Optional<Transaction> findByPaymentAndPaymentProvider(Payment payment, PaymentProvider paymentProvider);

    Optional<Transaction> findByClickTransId(String clickTransId);
}
