package com.example.paymal.repositories;

import com.example.paymal.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Role findByRoleName(String roleName);
}