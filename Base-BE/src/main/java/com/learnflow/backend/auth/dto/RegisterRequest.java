package com.learnflow.backend.auth.dto;

import jakarta.validation.constraints.*;

public record RegisterRequest(
    @NotBlank @Email @Size(max = 255) String email,
    @NotBlank @Size(min = 8, max = 72) String password,
    @NotBlank @Size(max = 120) String fullName
) {}
