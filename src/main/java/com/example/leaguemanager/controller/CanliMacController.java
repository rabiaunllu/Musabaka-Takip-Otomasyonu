package com.example.leaguemanager.controller;

import com.example.leaguemanager.model.DataStore;
import com.example.leaguemanager.model.Kullanici;
import com.example.leaguemanager.model.Mac;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class CanliMacController {

    @FXML private Label lblEvSahibi;
    @FXML private Label lblDeplasman;
    @FXML private Label lblEvSahibiSkor;
    @FXML private Label lblDeplasmanSkor;
    @FXML private Label lblSure; // FXML ID: lblSure (Süre)
    @FXML private Label lblMacZamani; // Üst başlık zaman etiketi
    @FXML private Circle sinyalDairesi;
    @FXML private VBox vboxOlaylar;
    @FXML private VBox vboxYoneticiKontrolleri;
    
    @FXML private Button btnBaslat;
    @FXML private Button btnDurdur;

    private Mac mevcutMac;
    private int macSaniyesi = 0;
    private int evSahibiSkor = 0;
    private int deplasmanSkor = 0;
    private Timeline zamanlayici;
    private boolean zamanlayiciCalisiyorMu = false;
    private static final String BILGI_RENGI = "#cccccc";

    @FXML
    public void initialize() {
        sinyalAnimasyonunuBaslat();
        siradakiMaciYukle();
        
        // Rol tabanlı görünürlük
        if (DataStore.getInstance().mevcutKullaniciyiGetir() != null &&
            DataStore.getInstance().mevcutKullaniciyiGetir().getRol() == Kullanici.Role.USER) {
            vboxYoneticiKontrolleri.setVisible(false);
        }

        // Rol tabanlı zamanlayıcı hızı
        Duration tickDuration = Duration.seconds(1); // Varsayılan: 90 dakika = 90 gerçek saniye
        if (DataStore.getInstance().mevcutKullaniciyiGetir() != null &&
            DataStore.getInstance().mevcutKullaniciyiGetir().getRol() == Kullanici.Role.DEVELOPER) {
            // Geliştirici hızı: daha hızlı ilerleme
            tickDuration = Duration.millis(11); 
        }

        zamanlayici = new Timeline(new KeyFrame(tickDuration, e -> {
            macSaniyesi++;
            sureEtiketiniGuncelle();
            
            // Maç duraklatma kuralları (45, 90, 105. dakikalarda durdurma simülasyonu)
            if (macSaniyesi == 2700) { // 45:00
                sureyiDurdur();
                olayKaydet("Devre Arası (45:00)", BILGI_RENGI);
            } else if (macSaniyesi == 5400) { // 90:00
                sureyiDurdur();
                olayKaydet("Normal Süre Bitti (90:00)", BILGI_RENGI);
            } else if (macSaniyesi == 6300) { // 105:00
                sureyiDurdur();
                olayKaydet("Uzatma Devre Arası (105:00)", BILGI_RENGI);
            } else if (macSaniyesi == 7200) { // 120:00
                maciBitir(); // Otomatik bitir
            }
        }));
        zamanlayici.setCycleCount(Animation.INDEFINITE);
    }

    private void siradakiMaciYukle() {
        // Oynanmamış ilk maçı bul
        mevcutMac = null;
        for (Mac m : DataStore.getInstance().maclariGetir()) {
            if (!m.isOynandiMi()) {
                mevcutMac = m;
                break;
            }
        }

        if (mevcutMac == null) {
            uyariGoster("Sezon Tamamlandı", "Tüm maçlar oynandı!");
            lblEvSahibi.setText("-");
            lblDeplasman.setText("-");
            return;
        }

        lblEvSahibi.setText(mevcutMac.getEvSahibi().getAd());
        lblDeplasman.setText(mevcutMac.getDeplasman().getAd());
        evSahibiSkor = 0;
        deplasmanSkor = 0;
        macSaniyesi = 0;
        skorEtiketleriniGuncelle();
        sureEtiketiniGuncelle();
        vboxOlaylar.getChildren().clear();
    }

    private void sinyalAnimasyonunuBaslat() {
        if (sinyalDairesi != null) {
            ScaleTransition st = new ScaleTransition(Duration.seconds(0.8), sinyalDairesi);
            st.setFromX(1.0);
            st.setFromY(1.0);
            st.setToX(1.5);
            st.setToY(1.5);
            st.setAutoReverse(true);
            st.setCycleCount(Animation.INDEFINITE);
            st.play();
        }
    }

    private void sureEtiketiniGuncelle() {
        int dk = macSaniyesi / 60;
        int sn = macSaniyesi % 60;
        String sureMetni = String.format("%02d:%02d", dk, sn);

        if (lblSure != null) {
            lblSure.setText(sureMetni);
        }
        
        if (lblMacZamani != null) {
            lblMacZamani.setText("CANLI - " + sureMetni);
        }
    }

    private void skorEtiketleriniGuncelle() {
        lblEvSahibiSkor.setText(String.valueOf(evSahibiSkor));
        lblDeplasmanSkor.setText(String.valueOf(deplasmanSkor));
    }

    @FXML
    public void sureyiBaslat() {
        if (!zamanlayiciCalisiyorMu && mevcutMac != null) {
            zamanlayici.play();
            zamanlayiciCalisiyorMu = true;
            btnBaslat.setDisable(true);
            btnDurdur.setDisable(false);
            
            if (macSaniyesi == 0) {
                olayKaydet("Maç Başladı!", BILGI_RENGI);
            } else {
                olayKaydet("Maç Devam Ediyor...", BILGI_RENGI);
            }
        }
    }

    @FXML
    public void sureyiDurdur() {
        if (zamanlayiciCalisiyorMu) {
            zamanlayici.pause();
            zamanlayiciCalisiyorMu = false;
            btnBaslat.setDisable(false);
            btnDurdur.setDisable(true);
            olayKaydet("Maç Duraklatıldı.", BILGI_RENGI);
        }
    }

    @FXML
    public void evSahibiGolEkle() {
        if (mevcutMac == null) return;
        evSahibiSkor++;
        skorEtiketleriniGuncelle();
        olayKaydet("GOL! " + mevcutMac.getEvSahibi().getAd(), "#4ade80");
        skoruParlat(lblEvSahibiSkor);
    }

    @FXML
    public void deplasmanGolEkle() {
        if (mevcutMac == null) return;
        deplasmanSkor++;
        skorEtiketleriniGuncelle();
        olayKaydet("GOL! " + mevcutMac.getDeplasman().getAd(), "#e879f9");
        skoruParlat(lblDeplasmanSkor);
    }

    @FXML
    public void evSahibiKartEkle() {
        if (mevcutMac == null) return;
        olayKaydet("Sarı Kart - " + mevcutMac.getEvSahibi().getAd(), "#facc15");
    }

    @FXML
    public void deplasmanKartEkle() {
        if (mevcutMac == null) return;
        olayKaydet("Sarı Kart - " + mevcutMac.getDeplasman().getAd(), "#facc15");
    }
    
    @FXML
    public void evSahibiKirmiziKartEkle() {
        if (mevcutMac == null) return;
        olayKaydet("KIRMIZI KART - " + mevcutMac.getEvSahibi().getAd(), "#ef4444");
    }

    @FXML
    public void deplasmanKirmiziKartEkle() {
        if (mevcutMac == null) return;
        olayKaydet("KIRMIZI KART - " + mevcutMac.getDeplasman().getAd(), "#ef4444");
    }

    @FXML
    public void maciBitir() {
        if (mevcutMac == null) return;

        zamanlayici.stop();
        zamanlayiciCalisiyorMu = false;
        btnBaslat.setDisable(false);
        
        // Sonucu kaydet
        mevcutMac.setEvSahibiSkor(evSahibiSkor);
        mevcutMac.setDeplasmanSkor(deplasmanSkor);
        mevcutMac.setOynandiMi(true);
        DataStore.getInstance().veriyiKaydet();

        uyariGoster("Maç Bitti", "Sonuç: " + evSahibiSkor + " - " + deplasmanSkor + "\nVeritabanına kaydedildi.");
        
        siradakiMaciYukle();
    }

    private void olayKaydet(String metin, String renk) {
        HBox satir = new HBox(10);
        satir.setStyle("-fx-background-color: rgba(30, 41, 59, 0.8); -fx-border-color: rgba(51, 65, 85, 1); -fx-background-radius: 5; -fx-border-radius: 5; -fx-padding: 10;");
        satir.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        int dk = macSaniyesi / 60;
        Label sureLbl = new Label(dk + "'");
        sureLbl.setMinWidth(30);
        sureLbl.setStyle("-fx-text-fill: #22d3ee; -fx-font-weight: bold;");
        
        Label olayLbl = new Label(metin);
        if (renk != null && renk.startsWith("#")) {
             olayLbl.setStyle("-fx-text-fill: " + renk + "; -fx-font-size: 13px;");
        } else {
             olayLbl.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        }

        satir.getChildren().addAll(sureLbl, olayLbl);
        vboxOlaylar.getChildren().add(0, satir);
    }

    private void skoruParlat(Label etiket) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), etiket);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.5);
        st.setToY(1.5);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    private void uyariGoster(String baslik, String icerik) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(baslik);
        alert.setHeaderText(null);
        alert.setContentText(icerik);
        alert.showAndWait();
    }
}
