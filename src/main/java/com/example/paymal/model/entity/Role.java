package com.example.paymal.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role extends BaseEntity implements GrantedAuthority {
    @Column(
            unique = true,
            nullable = false
    )
    private String roleName;

    @Override
    public String getAuthority() {
        return roleName;
    }
}
