package com.example.leaguemanager.controller;

import com.example.leaguemanager.model.DataStore;
import com.example.leaguemanager.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckbox;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    @FXML
    public void initialize() {
        // Init logic if needed
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Lütfen tüm alanları doldurunuz.");
            return;
        }

        User validUser = DataStore.getInstance().getUsers().stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);

        if (validUser != null) {
            // Login Success
            DataStore.getInstance().setCurrentUser(validUser);
            
            if (rememberMeCheckbox.isSelected()) {
                DataStore.getInstance().saveSession(validUser);
            }

            navigateToMain();
        } else {
            showError("Hatalı kullanıcı adı veya şifre.");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void navigateToMain() {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/leaguemanager/MainLayout.fxml"));
            BorderPane root = fxmlLoader.load();
            Scene scene = new Scene(root, 1100, 800); // 1100x800 ideal for dashboard
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setTitle("Lig Yöneticisi - " + DataStore.getInstance().getCurrentUser().getUsername());
        } catch (IOException e) {
            e.printStackTrace();
            showError("Sayfa yüklenirken hata oluştu.");
        }
    }
}
