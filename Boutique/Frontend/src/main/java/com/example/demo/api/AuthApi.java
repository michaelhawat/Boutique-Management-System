package com.example.demo.api;

import com.example.demo.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.http.HttpResponse;

public final class AuthApi {
    private AuthApi() {}

    private static final ObjectMapper M = new ObjectMapper();

    private static String path(String key, String def) {
        return ClientProps.getOr(key, def);
    }

    public static AuthResponse login(LoginRequest req) throws Exception {
        String body = M.writeValueAsString(req);
        HttpResponse<String> res = ApiClient.post(path("auth.login", "/api/auth/login"), body);
        
        int statusCode = res.statusCode();
        if (statusCode == 401) {
            String errorMsg = extractMessage(res.body());
            throw new RuntimeException(errorMsg != null && !errorMsg.isBlank() 
                ? errorMsg : "Invalid username or password");
        }
        if (statusCode != 200) {
            String errorMsg = extractMessage(res.body());
            throw new RuntimeException(errorMsg != null && !errorMsg.isBlank() 
                ? errorMsg : "HTTP " + statusCode + " - " + safe(res.body()));
        }

        return parseAuthResponse(res.body());
    }

    // AuthApi.java
    public static void register(RegisterRequest req) throws Exception {
        String body = M.writeValueAsString(req);
        HttpResponse<String> res = ApiClient.post(path("auth.register", "/api/auth/register"), body);

        int code = res.statusCode();
        if (code / 100 != 2) {
            String msg = extractMessage(res.body());

            // Friendly defaults for common cases
            if (code == 409) {
                if (msg == null || msg.isBlank()) msg = "Username or email already exists.";
                throw new RuntimeException(msg);
            } else if (code == 400) {
                if (msg == null || msg.isBlank()) msg = "Please check your inputs.";
                throw new RuntimeException(msg);
            }
            throw new RuntimeException("Error (" + code + "): " + (msg == null ? "" : msg));
        }
    }

    private static String extractMessage(String body) {
        try {
            if (body == null || body.isBlank()) return "";
            var n = M.readTree(body);
            if (n.has("message")) return n.get("message").asText();
            if (n.has("error"))   return n.get("error").asText();
            if (n.isTextual())    return n.asText();
            // sometimes servers return a list of validation messages:
            if (n.has("errors") && n.get("errors").isArray() && n.get("errors").size() > 0) {
                return n.get("errors").get(0).asText();
            }
        } catch (Exception ignore) {}
        return body == null ? "" : body;
    }




    public static void resetPassword(String email, String newPassword) throws Exception {
        ObjectNode n = M.createObjectNode();
        n.put("email", email == null ? "" : email.trim());
        n.put("newPassword", newPassword == null ? "" : newPassword.trim());

        HttpResponse<String> res = ApiClient.post(
                path("auth.reset", "/api/auth/password/reset"),
                M.writeValueAsString(n)
        );
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + " - " + safe(res.body()));
        }
    }




    public static MeResponse me() throws Exception {
        HttpResponse<String> res = ApiClient.get(path("me", "/api/me"));
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + " - " + safe(res.body()));
        }
        JsonNode n = M.readTree(res.body());
        if (n.hasNonNull("data")) n = n.get("data");     // handle wrapped responses
        return M.treeToValue(n, MeResponse.class);       // tolerant mapping
    }


    // ------- helpers -------

    private static AuthResponse parseAuthResponse(String json) throws Exception {
        JsonNode n = M.readTree(json);
        
        // Parse user info from response
        Long id = n.has("id") ? n.get("id").asLong() : null;
        String username = n.has("username") ? n.get("username").asText() : null;
        String email = n.has("email") ? n.get("email").asText() : null;
        String firstName = n.has("firstName") ? n.get("firstName").asText() : null;
        String lastName = n.has("lastName") ? n.get("lastName").asText() : null;
        
        return new AuthResponse(false, null, id, username, email, firstName, lastName);
    }

    private static String safe(String s) {
        return (s == null || s.isBlank()) ? "<empty body>" : s;
    }
}
