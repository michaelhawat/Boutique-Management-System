package com.example.demo;

import javafx.application.Application;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        // Initialize the global launcher with the primary stage
        Launcher.init(stage);

        stage.setMinWidth(1200);
        stage.setMinHeight(800);

        Launcher.go("login.fxml", "Pub Management Login");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
