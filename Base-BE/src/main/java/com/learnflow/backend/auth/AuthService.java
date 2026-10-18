package com.learnflow.backend.auth;

import com.learnflow.backend.auth.domain.*;
import com.learnflow.backend.auth.dto.*;
import com.learnflow.backend.common.web.ApiException;
import java.time.Clock;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {
    private final AuthRepository repository; private final PasswordEncoder encoder; private final JwtService jwt; private final Clock clock;
    public AuthService(AuthRepository repository, PasswordEncoder encoder, JwtService jwt, Clock clock) {
        this.repository = repository; this.encoder = encoder; this.jwt = jwt; this.clock = clock;
    }

    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (repository.existsByEmailIgnoreCase(email)) throw new ApiException(HttpStatus.CONFLICT, "Email is already registered");
        AppUser user = repository.save(new AppUser(email, encoder.encode(request.password()), request.fullName().trim(), Role.USER, clock.instant()));
        return response(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        AppUser user = repository.findByEmailIgnoreCase(request.email().trim())
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (!encoder.matches(request.password(), user.getPasswordHash())) throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        return response(user);
    }

    @Transactional(readOnly = true)
    public UserResponse current(Long userId) { return toResponse(requireUser(userId)); }

    @Transactional(readOnly = true)
    public AppUser requireUser(Long userId) {
        return repository.findById(userId).orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User no longer exists"));
    }

    private AuthResponse response(AppUser user) {
        JwtService.Token token = jwt.issue(user);
        return new AuthResponse(token.value(), "Bearer", token.expiresAt(), toResponse(user));
    }
    private UserResponse toResponse(AppUser user) { return new UserResponse(user.getId(), user.getEmail(), user.getFullName(), user.getRole().name(), user.getCreatedAt()); }
}
