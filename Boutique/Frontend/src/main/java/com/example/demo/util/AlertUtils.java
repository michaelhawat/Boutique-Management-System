package com.example.demo.util;


import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;


public final class AlertUtils {
    private AlertUtils() {}


    public static void info(String msg) { show(Alert.AlertType.INFORMATION, "Info", msg); }
    public static void warn(String msg) { show(Alert.AlertType.WARNING, "Warning", msg); }
    public static void error(String msg) { show(Alert.AlertType.ERROR, "Error", msg); }

    public static boolean confirm(String msg) {
        final boolean[] result = {false};
        Runnable r = () -> {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL);
            a.setHeaderText(null);
            a.setTitle("Confirm");
            Optional<ButtonType> choice = a.showAndWait();
            result[0] = choice.isPresent() && choice.get() == ButtonType.OK;
        };

        if (Platform.isFxApplicationThread()) r.run(); else Platform.runLater(r);

        // ⚠ Note: This blocks until the dialog closes
        return result[0];
    }
    private static void show(Alert.AlertType type, String title, String msg) {
        Runnable r = () -> {
            Alert a = new Alert(type, msg, ButtonType.OK);
            a.setHeaderText(null);
            a.setTitle(title);
            a.show();
        };
        if (Platform.isFxApplicationThread()) r.run(); else Platform.runLater(r);
    }
}