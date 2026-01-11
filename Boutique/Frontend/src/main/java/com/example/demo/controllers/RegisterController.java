package com.example.demo.controllers;

import com.example.demo.Launcher;
import com.example.demo.api.AuthApi;
import com.example.demo.model.RegisterRequest;
import com.example.demo.util.AlertUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class RegisterController {

    @FXML private TextField username;
    @FXML private TextField email;
    @FXML private PasswordField password;
    @FXML private PasswordField confirmPassword;
    @FXML private TextField firstName;
    @FXML private TextField lastName;
    @FXML private TextField phone;           // optional


    @FXML
    public void initialize() {
        // Add Enter key support - pressing Enter in any field triggers register
        EventHandler<KeyEvent> enterHandler = event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onRegister();
            }
        };

        username.setOnKeyPressed(enterHandler);
        email.setOnKeyPressed(enterHandler);
        password.setOnKeyPressed(enterHandler);
        confirmPassword.setOnKeyPressed(enterHandler);
        firstName.setOnKeyPressed(enterHandler);
        lastName.setOnKeyPressed(enterHandler);
        phone.setOnKeyPressed(enterHandler);
    }
    @FXML
    private void onRegister() {
        try {
            String u  = safe(username);
            String e  = safe(email);
            String p  = safe(password);
            String cp = safe(confirmPassword);
            String f  = safe(firstName);
            String l  = safe(lastName);
            String ph = safe(phone);

            // required checks
            if (u.isEmpty() || e.isEmpty() || p.isEmpty() || cp.isEmpty()) {
                AlertUtils.warn("Fill all required fields (username, email, password, confirm).");
                return;
            }
            if (!p.equals(cp)) {
                AlertUtils.warn("Passwords do not match.");
                return;
            }


            // map to backend enum values

            RegisterRequest req = new RegisterRequest(
                    u,           // username
                    e,           // email
                    p,           // password
                    emptyToNull(f),
                    emptyToNull(l),
                    emptyToNull(ph)// role must be enum string
            );

            AuthApi.register(req);
            AlertUtils.info("Account created. Please login.");
            Launcher.go("login.fxml", "Login");
        } catch (Exception ex) {
            com.example.demo.util.AlertUtils.warn(ex.getMessage());
        }
    }

    @FXML
    private void goLogin() {
        Launcher.go("login.fxml", "Login");
    }

    // helpers
    private static String safe(TextField t) {
        return (t == null || t.getText() == null) ? "" : t.getText().trim();
    }
    private static String safe(PasswordField t) {
        return (t == null || t.getText() == null) ? "" : t.getText().trim();
    }
    private static String emptyToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

}
