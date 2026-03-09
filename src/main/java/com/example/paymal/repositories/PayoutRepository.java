package com.example.paymal.repositories;

import com.example.paymal.model.entity.Payout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PayoutRepository extends JpaRepository<Payout, UUID> {
}
