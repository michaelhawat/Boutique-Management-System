package com.example.demo.controllers;

import com.example.demo.Launcher;
import com.example.demo.util.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ForgotController {
    @FXML private TextField emailOrUsername;

    public void onSend(){
        try {
            String v = emailOrUsername.getText()==null? "": emailOrUsername.getText().trim();
            if (v.isEmpty()) { AlertUtils.warn("Enter your email."); return; }
            if (!v.contains("@")) { AlertUtils.warn("Please enter a valid email address."); return; }

            // Navigate to reset screen - user will enter new password directly
            Launcher.go("reset.fxml", "Reset Password");
        } catch (Exception ex) {
            AlertUtils.error("Error: " + ex.getMessage());
        }
    }

    public void goLogin(){ Launcher.go("login.fxml", "Login"); }
}
