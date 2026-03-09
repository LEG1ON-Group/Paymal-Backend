package com.example.paymal.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Builder
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity implements UserDetails {

    @Column(unique = true, nullable = false)
    private String phone;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Role role;

    @com.fasterxml.jackson.annotation.JsonProperty("role")
    public String getRoleName() {
        return role != null ? role.getRoleName() : null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return phone;
    }

    @Override
    @com.fasterxml.jackson.annotation.JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role != null) {
            return Collections.singleton(role);
        }
        return Collections.emptyList();
    }

    @Override
    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}