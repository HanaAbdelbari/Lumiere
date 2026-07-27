package com.marketplace.lumiere.admin;

import com.marketplace.lumiere.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    private final JwtService jwtService;
    private final String adminPassword;

    public AdminAuthController(
            JwtService jwtService,
            @Value("${lumiere.admin.password}") String adminPassword) {
        this.jwtService = jwtService;
        this.adminPassword = adminPassword;
    }

    // POST /api/admin/login  { "password": "..." }
    // Returns a JWT if the password is correct.
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (request.password() == null || !request.password().equals(adminPassword)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Incorrect password"));
        }
        String token = jwtService.generateToken("admin");
        return ResponseEntity.ok(Map.of("token", token));
    }

    public record LoginRequest(String password) {
    }
}