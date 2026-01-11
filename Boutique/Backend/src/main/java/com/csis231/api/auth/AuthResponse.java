// AuthResponse.java
package com.csis231.api.auth;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class AuthResponse {
    private String token; // kept for compatibility but will be null
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}
