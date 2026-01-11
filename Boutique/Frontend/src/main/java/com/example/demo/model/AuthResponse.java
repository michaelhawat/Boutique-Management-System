package com.example.demo.model;

public record AuthResponse(
    boolean otpRequired, // kept for compatibility, always false now
    String token,        // kept for compatibility, always null now
    Long id,
    String username,
    String email,
    String firstName,
    String lastName
) {
    // Constructor for backward compatibility
    public AuthResponse(boolean otpRequired, String token) {
        this(otpRequired, token, null, null, null, null, null);
    }
}
