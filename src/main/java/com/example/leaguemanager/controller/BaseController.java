package com.example.leaguemanager.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

// Tüm kontrolcüler için temel sınıf (Miras Alma Örneği)
public abstract class BaseController {

    // Ortak uyarı gösterme metodu
    protected void uyariGoster(String baslik, String icerik) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(baslik);
        alert.setHeaderText(null);
        alert.setContentText(icerik);
        alert.showAndWait();
    }
    
    // Hata mesajı gösterme metodu
    protected void hataGoster(String baslik, String icerik) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(baslik);
        alert.setHeaderText(null);
        alert.setContentText(icerik);
        alert.showAndWait();
    }

    // Onay (Confirmation) dialogu
    protected boolean onayIste(String baslik, String icerik) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(baslik);
        alert.setHeaderText(null);
        alert.setContentText(icerik);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
