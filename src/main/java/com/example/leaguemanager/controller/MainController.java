package com.example.leaguemanager.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;

public class MainController {

    private static MainController instance;

    public static MainController getInstance() {
        return instance;
    }

    @FXML
    private AnchorPane anaIcerikAlani;

    @FXML
    private javafx.scene.control.Button btnDashboard;
    @FXML
    private javafx.scene.control.Button btnTeams;
    @FXML
    private javafx.scene.control.Button btnFixtures;
    @FXML
    private javafx.scene.control.Button btnTable;
    @FXML
    private javafx.scene.control.Button btnLive;
    @FXML
    private javafx.scene.control.Button userBadge;

    @FXML
    public void initialize() {
        instance = this;
        showDashboard();
        
        // Display Current User Badge
        com.example.leaguemanager.model.User current = com.example.leaguemanager.model.DataStore.getInstance().getCurrentUser();
        if (current != null) {
            if (current.getRole().name().equals("USER")) {
                btnLive.setVisible(false);
                btnLive.setManaged(false); // Remove space too
            }
            
            String roleText = "Kullanıcı";
            String color = "#64748b"; // Gray for USER

            if (current.getRole() == com.example.leaguemanager.model.User.Role.ADMIN) {
                roleText = "Yönetici";
                color = "#0ea5e9"; // Blue for ADMIN
            } else if (current.getRole() == com.example.leaguemanager.model.User.Role.DEVELOPER) {
                roleText = "Geliştirici";
                color = "#8b5cf6"; // Purple for DEVELOPER
            }
            
            userBadge.setText(roleText + ": " + current.getUsername());
            userBadge.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-weight: bold;");
        }
    }

    @FXML
    public void showDashboard() {
        loadPage("GosterimPaneli.fxml");
        setActiveButton(btnDashboard);
    }

    @FXML
    public void showTeams() {
        loadPage("Takimlar.fxml");
        setActiveButton(btnTeams);
    }

    @FXML
    public void showFixtures() {
        loadPage("Fikstur.fxml");
        setActiveButton(btnFixtures);
    }

    @FXML
    public void showLeagueTable() {
        loadPage("LigTablosu.fxml");
        setActiveButton(btnTable);
    }

    @FXML
    public void showLiveMatch() {
        loadPage("CanliMac.fxml");
        setActiveButton(btnLive);
    }

    @FXML
    public void handleLogout() {
        com.example.leaguemanager.model.DataStore.getInstance().logout();
        try {
            javafx.stage.Stage stage = (javafx.stage.Stage) userBadge.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/leaguemanager/Login.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(fxmlLoader.load(), 1100, 800);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setTitle("Lig Yöneticisi - Giriş");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void setActiveButton(javafx.scene.control.Button activeButton) {
        // Reset all buttons to default style
        resetButtonStyle(btnDashboard);
        resetButtonStyle(btnTeams);
        resetButtonStyle(btnFixtures);
        resetButtonStyle(btnTable);
        resetButtonStyle(btnLive);

        // Set active style for the clicked button
        activeButton.getStyleClass().add("menu-button-active");
    }

    private void resetButtonStyle(javafx.scene.control.Button button) {
        if (button != null) {
            button.getStyleClass().remove("menu-button-active");
            if (!button.getStyleClass().contains("menu-button")) {
                button.getStyleClass().add("menu-button");
            }
        }
    }

    private void loadPage(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/leaguemanager/" + fxmlFileName));
            AnchorPane view = loader.load();
            
            // İçeriği eklemeden önce temizle
            anaIcerikAlani.getChildren().clear();
            
            // Yeni sayfayı ekle
            anaIcerikAlani.getChildren().add(view);
            
            // Sayfanın tüm alanı kaplamasını sağla
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
