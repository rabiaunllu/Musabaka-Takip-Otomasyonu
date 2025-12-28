package com.example.leaguemanager;

import com.example.leaguemanager.model.DataStore;
import com.example.leaguemanager.model.Kullanici;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

// Uygulamanın ana giriş sınıfı
public class HelloApplication extends Application {
    
    @Override
    public void start(Stage stage) throws IOException {
        // Daha önceki oturumu yüklemeye çalış
        Kullanici kayitliKullanici = DataStore.getInstance().oturumuYukle();
        
        String fxmlDosyasi = "Giris.fxml";
        String baslik = "Süper Lig Otomasyonu - Giriş";

        // Eğer oturum varsa direkt ana sayfaya git
        if (kayitliKullanici != null) {
            fxmlDosyasi = "MainLayout.fxml";
            baslik = "Süper Lig Otomasyonu - " + kayitliKullanici.getKullaniciAdi();
            DataStore.getInstance().mevcutKullaniciAyarla(kayitliKullanici);
        }

        // Ekranı yükle
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlDosyasi));
        Scene scene = new Scene(loader.load(), 1100, 800);
        
        stage.setTitle(baslik);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
