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
    public void initialize() {
        instance = this;
        showDashboard();
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
