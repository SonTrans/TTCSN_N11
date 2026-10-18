package com.learnflow.backend.auth.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "app_user")
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true) private String email;
    @Column(name = "password_hash", nullable = false) private String passwordHash;
    @Column(name = "full_name", nullable = false) private String fullName;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Role role;
    @Column(name = "created_at", nullable = false) private Instant createdAt;

    protected AppUser() {}
    public AppUser(String email, String passwordHash, String fullName, Role role, Instant createdAt) {
        this.email = email; this.passwordHash = passwordHash; this.fullName = fullName; this.role = role; this.createdAt = createdAt;
    }
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getFullName() { return fullName; }
    public Role getRole() { return role; }
    public Instant getCreatedAt() { return createdAt; }
}
