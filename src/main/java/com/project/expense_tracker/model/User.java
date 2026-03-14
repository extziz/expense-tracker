package com.project.expense_tracker.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ── Constructors ─────────────────────────────────────────────────────────

    public User() {}

    public User(String username, String email, String password, Role role) {
        this.username = username;
        this.email    = email;
        this.password = password;
        this.role     = role;
    }

    // ── UserDetails contract ─────────────────────────────────────────────────
    // These tell Spring Security about the account's state.
    // All return true for now — later you can add email verification, account locking, etc.

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring expects "ROLE_" prefix — so @PreAuthorize("hasRole('ADMIN')") works
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public Long getId()                       { return id; }
    public void setId(Long id)                { this.id = id; }

    @Override
    public String getUsername()               { return username; }
    public void setUsername(String username)  { this.username = username; }

    public String getEmail()                  { return email; }
    public void setEmail(String email)        { this.email = email; }

    @Override
    public String getPassword()               { return password; }
    public void setPassword(String password)  { this.password = password; }

    public Role getRole()                     { return role; }
    public void setRole(Role role)            { this.role = role; }

    public LocalDateTime getCreatedAt()       { return createdAt; }
}