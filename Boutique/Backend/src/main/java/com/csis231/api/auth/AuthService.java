package com.csis231.api.auth;

import com.csis231.api.user.User;
import com.csis231.api.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest req) {
        // Fetch user by username or email
        User user = userRepository.findByUsername(req.getUsername())
                .or(() -> userRepository.findByEmail(req.getUsername()))
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        // Verify password
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        // Check if user is active
        if (user.getIsActive() == null || !user.getIsActive()) {
            throw new BadCredentialsException("Account is disabled");
        }

        // Return user info without token
        return new AuthResponse(
                null, // no token
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    /** Reset password with new password */
    @Transactional
    public void updatePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Unknown email"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
