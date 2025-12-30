package com.example.leaguemanager.controller;

import com.example.leaguemanager.model.DataStore;
import com.example.leaguemanager.model.Kullanici;
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

// Giriş ekranı kontrolcüsü
public class GirisController extends BaseController {

    @FXML private TextField txtKullaniciAdi;    // Kullanıcı adı kutusu
    @FXML private PasswordField txtSifre;       // Şifre kutusu
    @FXML private CheckBox chkBeniHatirla;       // Beni hatırla seçeneği
    @FXML private Label lblHata;                // Hata mesajı etiketi
    @FXML private Button btnGiris;              // Giriş butonu

    @FXML
    public void initialize() {
        // Gerekirse başlangıç ayarları buraya yazılır
    }

    // Giriş butonuna basıldığında çalışan metod
    @FXML
    private void girisYap() {
        String kullaniciAdi = txtKullaniciAdi.getText();
        String sifre = txtSifre.getText();

        // Boş bırakıldı mı kontrolü
        if (kullaniciAdi.isEmpty() || sifre.isEmpty()) {
            hataGoster("Lütfen tüm alanları doldurunuz.");
            return;
        }

        // Kullanıcıyı bulma
        Kullanici bulunanKullanici = null;
        for (Kullanici u : DataStore.getInstance().kullanicilariGetir()) {
            if (u.getKullaniciAdi().equals(kullaniciAdi) && u.getSifre().equals(sifre)) {
                bulunanKullanici = u;
                break;
            }
        }

        if (bulunanKullanici != null) {
            // Giriş Başarılı
            DataStore.getInstance().mevcutKullaniciAyarla(bulunanKullanici);
            
            // Beni hatırla seçiliyse oturumu kaydet
            if (chkBeniHatirla.isSelected()) {
                DataStore.getInstance().oturumuKaydet(bulunanKullanici);
            }

            // Ana ekrana git
            anaEkranaGit();
        } else {
            hataGoster("Kullanıcı adı veya şifre hatalı!");
        }
    }

    // Hata mesajını gösteren yardımcı metod
    private void hataGoster(String mesaj) {
        lblHata.setText(mesaj);
        lblHata.setVisible(true);
    }

    // Ana ekrana geçiş yapan metod
    private void anaEkranaGit() {
        try {
            Stage stage = (Stage) btnGiris.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/leaguemanager/MainLayout.fxml"));
            BorderPane root = loader.load();
            Scene scene = new Scene(root, 1100, 800);
            
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setTitle("Süper Lig Otomasyonu - " + DataStore.getInstance().mevcutKullaniciyiGetir().getKullaniciAdi());
        } catch (IOException e) {
            System.out.println("Ekran yükleme hatası!");
            hataGoster("Sayfa açılırken bir hata oluştu.");
        }
    }
}
