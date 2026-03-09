package com.example.paymal.repositories;

import com.example.paymal.model.entity.Merchant;
import com.example.paymal.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, UUID> {
    List<Merchant> findAllByUser(User user);

    Merchant findByApiKey(String apiKey);
}
