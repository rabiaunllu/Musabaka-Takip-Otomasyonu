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
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class CanliMacController extends BaseController {

    @FXML private Label lblEvSahibi;
    @FXML private Label lblDeplasman;
    @FXML private Label lblEvSahibiSkor;
    @FXML private Label lblDeplasmanSkor;
    @FXML private Label lblSure;
    @FXML private Label lblMacZamani;
    @FXML private Label lblUzatma;
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
    private int uzatmaDakikasi = 0;
    private static final String BILGI_RENGI = "#cccccc";

    private AudioClip düdükSesi;
    private AudioClip bitisDüdükSesi;

    @FXML
    public void initialize() {
        düdükSesiYukle();
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
            
            int uzatmaSaniyesi = uzatmaDakikasi * 60;
            
            // Maç duraklatma kuralları (Uzatma dahil)
            // Segment korumaları: Uzatma süresinin bir sonraki devrenin başlangıcını geçmemesi için makul sınırlar (örn: 45+30 dk)
            if (macSaniyesi == 2700 + uzatmaSaniyesi) { // 1. Devre Sonu (45+X)
                sureyiDurdur();
                olayKaydet("Devre Arası (" + (45 + uzatmaDakikasi) + ":00)", BILGI_RENGI);
                düdükÇal();
                uzatmaSifirla();
                macSaniyesi = 2700; // Sayacı 45:00'a geri çek
                sureEtiketiniGuncelle();
            } else if (macSaniyesi == 5400 + uzatmaSaniyesi && macSaniyesi >= 4500) { // 2. Devre Sonu (90+X)
                sureyiDurdur();
                olayKaydet("Normal Süre Bitti (" + (90 + uzatmaDakikasi) + ":00)", BILGI_RENGI);
                düdükÇal();
                uzatmaSifirla();
                macSaniyesi = 5400; // Sayacı 90:00'a geri çek
                sureEtiketiniGuncelle();
            } else if (macSaniyesi == 6300 + uzatmaSaniyesi && macSaniyesi >= 6000) { // Uzatma 1. Devre Sonu (105+X)
                sureyiDurdur();
                olayKaydet("Uzatma Devre Arası (" + (105 + uzatmaDakikasi) + ":00)", BILGI_RENGI);
                düdükÇal();
                uzatmaSifirla();
                macSaniyesi = 6300; // Sayacı 105:00'a geri çek
                sureEtiketiniGuncelle();
            } else if (macSaniyesi >= 7200 + uzatmaSaniyesi && macSaniyesi >= 7000) { // Uzatma 2. Devre Sonu / Maç Sonu (120+X)
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
                // Eğer maçta "BAY" takımı varsa direkt geçmiş say
                if (m.getEvSahibi().getAd().equals("BAY") || m.getDeplasman().getAd().equals("BAY")) {
                    m.setOynandiMi(true);
                    m.setEvSahibiSkor(0);
                    m.setDeplasmanSkor(0);
                    DataStore.getInstance().veriyiKaydet();
                    continue; // Bir sonrakine bak
                }
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
        uzatmaSifirla();
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
            String gostergeMetni = String.format("CANLI - %d'", dk);
            lblMacZamani.setText(gostergeMetni);
        }
        
        if (lblUzatma != null) {
            if (uzatmaDakikasi > 0) {
                lblUzatma.setText("+" + uzatmaDakikasi);
                lblUzatma.setVisible(true);
            } else {
                lblUzatma.setVisible(false);
            }
        }
    }

    @FXML
    public void uzatmaEkle() {
        uzatmaDakikasi++;
        sureEtiketiniGuncelle();
    }

    @FXML
    public void uzatmaSifirla() {
        uzatmaDakikasi = 0;
        sureEtiketiniGuncelle();
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
                düdükÇal();
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
        // Skor güncellemesini animasyon içine taşıdık
        olayKaydet("GOL! " + mevcutMac.getEvSahibi().getAd(), "#4ade80");
        animasyonluSkorGuncelle(lblEvSahibiSkor, evSahibiSkor);
    }

    @FXML
    public void deplasmanGolEkle() {
        if (mevcutMac == null) return;
        deplasmanSkor++;
        // Skor güncellemesini animasyon içine taşıdık
        olayKaydet("GOL! " + mevcutMac.getDeplasman().getAd(), "#e879f9");
        animasyonluSkorGuncelle(lblDeplasmanSkor, deplasmanSkor);
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
        
        bitisDüdügüÇal();
        uyariGoster("Maç Bitti", "Sonuç: " + evSahibiSkor + " - " + deplasmanSkor + "\nVeritabanına kaydedildi.");
        
        siradakiMaciYukle();
    }

    private void düdükSesiYukle() {
        try {
            String yol = getClass().getResource("/com/example/leaguemanager/sounds/referee-whistle.mp3").toExternalForm();
            düdükSesi = new AudioClip(yol);
            
            String bitisYol = getClass().getResource("/com/example/leaguemanager/sounds/final-whistle.mp3").toExternalForm();
            bitisDüdükSesi = new AudioClip(bitisYol);
        } catch (Exception e) {
            System.err.println("Düdük sesleri yüklenemedi: " + e.getMessage());
        }
    }

    private void düdükÇal() {
        if (düdükSesi != null) {
            düdükSesi.play();
        }
    }

    private void bitisDüdügüÇal() {
        if (bitisDüdükSesi != null) {
            bitisDüdükSesi.play();
        }
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

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnSil = new Button("🗑");
        btnSil.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-cursor: hand; -fx-padding: 0 5 0 5; -fx-font-size: 14px;");
        btnSil.setVisible(false);

        // Rol kontrolü: Sadece yönetici ve geliştiriciler silebilir
        boolean yetkiliMi = DataStore.getInstance().mevcutKullaniciyiGetir() != null &&
                            DataStore.getInstance().mevcutKullaniciyiGetir().getRol() != Kullanici.Role.USER;

        if (yetkiliMi) {
            satir.setOnMouseEntered(e -> btnSil.setVisible(true));
            satir.setOnMouseExited(e -> btnSil.setVisible(false));
            
            btnSil.setOnAction(e -> {
                vboxOlaylar.getChildren().remove(satir);
                
                // Eğer silinen şey bir gol ise skoru geri çek
                if (metin.startsWith("GOL!")) {
                    if (metin.contains(mevcutMac.getEvSahibi().getAd())) {
                        if (evSahibiSkor > 0) evSahibiSkor--;
                    } else if (metin.contains(mevcutMac.getDeplasman().getAd())) {
                        if (deplasmanSkor > 0) deplasmanSkor--;
                    }
                    skorEtiketleriniGuncelle();
                }
            });
        }

        satir.getChildren().addAll(sureLbl, olayLbl, spacer, btnSil);
        vboxOlaylar.getChildren().add(0, satir);
    }

    private void animasyonluSkorGuncelle(Label etiket, int yeniSkor) {
        // 1. Aşama: Büyüt (Pop-up)
        ScaleTransition buyut = new ScaleTransition(Duration.millis(250), etiket);
        buyut.setFromX(1.0);
        buyut.setFromY(1.0);
        buyut.setToX(1.5);
        buyut.setToY(1.5);
        
        // Büyüme bitince rakamı değiştir
        buyut.setOnFinished(e -> {
            etiket.setText(String.valueOf(yeniSkor));
            
            // 2. Aşama: Küçült (Normal boyuta dön)
            ScaleTransition kucult = new ScaleTransition(Duration.millis(250), etiket);
            kucult.setFromX(1.5);
            kucult.setFromY(1.5);
            kucult.setToX(1.0);
            kucult.setToY(1.0);
            kucult.play();
        });
        
        buyut.play();
    }


}
