package com.example.demo.controllers;

import com.example.demo.Launcher;
import com.example.demo.api.AuthApi;
import com.example.demo.util.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ResetController {
    @FXML private TextField email;
    @FXML private PasswordField newPassword;
    @FXML private PasswordField confirmPassword;

    public void onReset() {
        try {
            String e = safe(email.getText());
            String p = safe(newPassword.getText());
            String cp = safe(confirmPassword.getText());

            if (e.isEmpty() || p.isEmpty() || cp.isEmpty()) {
                AlertUtils.warn("Please fill in all fields.");
                return;
            }

            if (!p.equals(cp)) {
                AlertUtils.warn("Passwords do not match.");
                return;
            }

            if (p.length() < 8) {
                AlertUtils.warn("Password must be at least 8 characters long.");
                return;
            }

            AuthApi.resetPassword(e, p);
            AlertUtils.info("Password reset successfully. Please login with your new password.");
            Launcher.go("login.fxml", "Login");
        } catch (Exception ex) {
            AlertUtils.error("Reset failed: " + ex.getMessage());
        }
    }

    private static String safe(String s){ return s == null ? "" : s.trim(); }

    public void goLogin(){ Launcher.go("login.fxml", "Login"); }
}
