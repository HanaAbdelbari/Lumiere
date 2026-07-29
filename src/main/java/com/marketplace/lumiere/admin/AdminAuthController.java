package com.marketplace.lumiere.admin;

import com.marketplace.lumiere.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    // Now stores a BCrypt HASH of the password, not the plain text.
    private final String adminPasswordHash;

    public AdminAuthController(
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            @Value("${lumiere.admin.password-hash}") String adminPasswordHash) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.adminPasswordHash = adminPasswordHash;
    }

    // POST /api/admin/login  { "password": "..." }
    // Compares the entered password against the stored BCrypt hash.
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (request.password() == null
                || !passwordEncoder.matches(request.password(), adminPasswordHash)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Incorrect password"));
        }
        String token = jwtService.generateToken("admin");
        return ResponseEntity.ok(Map.of("token", token));
    }

    public record LoginRequest(String password) {
    }
}