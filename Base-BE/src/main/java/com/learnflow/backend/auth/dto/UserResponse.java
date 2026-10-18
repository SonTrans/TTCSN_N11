package com.learnflow.backend.auth.dto;

import java.time.Instant;

public record UserResponse(Long id, String email, String fullName, String role, Instant createdAt) {}
