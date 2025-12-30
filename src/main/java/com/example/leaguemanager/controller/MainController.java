package com.example.leaguemanager.controller;

import com.example.leaguemanager.model.DataStore;
import com.example.leaguemanager.model.Kullanici;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;

public class MainController extends BaseController {

    private static MainController ornek;

    public static MainController getOrnek() {
        return ornek;
    }

    @FXML
    private AnchorPane anaIcerikAlani;

    @FXML
    private javafx.scene.control.Button btnGostergePaneli;
    @FXML
    private javafx.scene.control.Button btnTakimlar;
    @FXML
    private javafx.scene.control.Button btnFikstur;
    @FXML
    private javafx.scene.control.Button btnLigTablosu;
    @FXML
    private javafx.scene.control.Button btnCanliMac;
    @FXML
    private javafx.scene.control.Button lblKullaniciRozeti;

    @FXML
    public void initialize() {
        ornek = this;
        gostergePaneliniGoster();
        
        // Mevcut kullanıcı rozetini göster
        Kullanici mevcutKullanici = DataStore.getInstance().mevcutKullaniciyiGetir();
        if (mevcutKullanici != null) {
            if (mevcutKullanici.getRol() == Kullanici.Role.USER) {
                btnCanliMac.setVisible(false);
                btnCanliMac.setManaged(false); // Alanı da kaldır
            }
            
            String rolMetni = "Kullanıcı";
            String renk = "#64748b"; // USER için gri

            if (mevcutKullanici.getRol() == Kullanici.Role.ADMIN) {
                rolMetni = "Yönetici";
                renk = "#0ea5e9"; // ADMIN için mavi
            } else if (mevcutKullanici.getRol() == Kullanici.Role.DEVELOPER) {
                rolMetni = "Geliştirici";
                renk = "#8b5cf6"; // DEVELOPER için mor
            }
            
            lblKullaniciRozeti.setText(rolMetni + ": " + mevcutKullanici.getKullaniciAdi());
            lblKullaniciRozeti.setStyle("-fx-background-color: " + renk + "; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-weight: bold;");
        }
    }

    @FXML
    public void gostergePaneliniGoster() {
        sayfayıYukle("GosterimPaneli.fxml");
        aktifButonuAyarla(btnGostergePaneli);
    }

    @FXML
    public void takimlariGoster() {
        sayfayıYukle("Takimlar.fxml");
        aktifButonuAyarla(btnTakimlar);
    }

    @FXML
    public void fiksturuGoster() {
        sayfayıYukle("Fikstur.fxml");
        aktifButonuAyarla(btnFikstur);
    }

    @FXML
    public void ligTablosunuGoster() {
        sayfayıYukle("LigTablosu.fxml");
        aktifButonuAyarla(btnLigTablosu);
    }

    @FXML
    public void canliMaciGoster() {
        sayfayıYukle("CanliMac.fxml");
        aktifButonuAyarla(btnCanliMac);
    }

    @FXML
    public void cikisYap() {
        DataStore.getInstance().cikisYap();
        try {
            javafx.stage.Stage stage = (javafx.stage.Stage) lblKullaniciRozeti.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/leaguemanager/Giris.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(fxmlLoader.load(), 1100, 800);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setTitle("Lig Yöneticisi - Giriş");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void aktifButonuAyarla(javafx.scene.control.Button aktifButon) {
        // Tüm butonları varsayılan stile sıfırla
        butonStiliniSifirla(btnGostergePaneli);
        butonStiliniSifirla(btnTakimlar);
        butonStiliniSifirla(btnFikstur);
        butonStiliniSifirla(btnLigTablosu);
        butonStiliniSifirla(btnCanliMac);

        // Tıklanan butona aktif stilini uygula
        aktifButon.getStyleClass().add("menu-button-active");
    }

    private void butonStiliniSifirla(javafx.scene.control.Button buton) {
        if (buton != null) {
            buton.getStyleClass().remove("menu-button-active");
            if (!buton.getStyleClass().contains("menu-button")) {
                buton.getStyleClass().add("menu-button");
            }
        }
    }

    private void sayfayıYukle(String fxmlDosyaAdi) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/leaguemanager/" + fxmlDosyaAdi));
            AnchorPane gorunum = loader.load();
            
            // İçeriği eklemeden önce temizle
            anaIcerikAlani.getChildren().clear();
            
            // Yeni sayfayı ekle
            anaIcerikAlani.getChildren().add(gorunum);
            
            // Sayfanın tüm alanı kaplamasını sağla
            AnchorPane.setTopAnchor(gorunum, 0.0);
            AnchorPane.setBottomAnchor(gorunum, 0.0);
            AnchorPane.setLeftAnchor(gorunum, 0.0);
            AnchorPane.setRightAnchor(gorunum, 0.0);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
