package com.example.paymal.repositories;

import com.example.paymal.model.entity.Ledger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LedgerRepository extends JpaRepository<Ledger, UUID> {
}
