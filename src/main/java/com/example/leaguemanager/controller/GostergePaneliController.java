package com.example.leaguemanager.controller;

import com.example.leaguemanager.model.DataStore;
import com.example.leaguemanager.model.Mac;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Ana panel (Dashboard) kontrolcü sınıfı
public class GostergePaneliController {

    @FXML private Label lblToplamTakim;      // Toplam takım sayısı etiketi
    @FXML private Label lblToplamMac;        // Toplam maç sayısı etiketi
    @FXML private Label lblMevcutHafta;      // Mevcut hafta etiketi
    @FXML private VBox vboxYaklasanMaclar;   // Yaklaşan maçlar listesi
    @FXML private VBox vboxSonSonuclar;      // Sonuçlanan maçlar listesi

    @FXML
    public void initialize() {
        verileriYenile(); // Ekran açıldığında verileri tazele
    }

    // Ekrandaki verileri güncelleyen metod
    public void verileriYenile() {
        DataStore store = DataStore.getInstance();

        // 1. İstatistik kartlarını doldurma
        lblToplamTakim.setText(String.valueOf(store.takimlariGetir().size()));
        lblToplamMac.setText(String.valueOf(store.maclariGetir().size()));
        
        // Mevcut haftayı hesaplama (Oynanmamış maçın olduğu ilk hafta)
        int mevcutHafta = 1;
        if (!store.maclariGetir().isEmpty()) {
            int maxHafta = 0;
            for (Mac m : store.maclariGetir()) {
                if (m.getHafta() > maxHafta) {
                    maxHafta = m.getHafta();
                }
            }

            for (int h = 1; h <= maxHafta; h++) {
                int haftadakiMacSayisi = 0;
                int haftadakiOynananMacSayisi = 0;

                for (Mac m : store.maclariGetir()) {
                    if (m.getHafta() == h) {
                        haftadakiMacSayisi++;
                        if (m.isOynandiMi()) {
                            haftadakiOynananMacSayisi++;
                        }
                    }
                }

                if (haftadakiOynananMacSayisi < haftadakiMacSayisi) {
                    mevcutHafta = h;
                    break;
                } else if (h == maxHafta) {
                    mevcutHafta = maxHafta;
                }
            }
        }
        lblMevcutHafta.setText(String.valueOf(mevcutHafta));

        // 2. Yaklaşan maçları listeleme
        yaklasanMaclariDoldur(store.maclariGetir());

        // 3. Son sonuçları listeleme
        sonSonuclariDoldur(store.maclariGetir());
    }

    // Yaklaşan maçları panele ekleyen metod
    private void yaklasanMaclariDoldur(List<Mac> tumMaclar) {
        // Eski kayıtları temizle (Başlıklar hariç)
        if (vboxYaklasanMaclar.getChildren().size() > 2) {
            vboxYaklasanMaclar.getChildren().remove(2, vboxYaklasanMaclar.getChildren().size());
        }

        // Oynanmamış ilk 3 maçı bulma
        List<Mac> yaklasanlar = new ArrayList<>();
        for (Mac m : tumMaclar) {
            if (!m.isOynandiMi()) {
                yaklasanlar.add(m);
                if (yaklasanlar.size() == 3) break;
            }
        }

        for (Mac m : yaklasanlar) {
            HBox satir = yaklasanMacSatiriOlustur(m);
            vboxYaklasanMaclar.getChildren().add(satir);
        }
        
        if (yaklasanlar.isEmpty()) {
             Label bosLabel = new Label("Yaklaşan maç bulunamadı.");
             bosLabel.setStyle("-fx-text-fill: white; -fx-padding: 10;");
             vboxYaklasanMaclar.getChildren().add(bosLabel);
        }
    }

    // Son sonuçları panele ekleyen metod
    private void sonSonuclariDoldur(List<Mac> tumMaclar) {
        if (vboxSonSonuclar.getChildren().size() > 2) {
            vboxSonSonuclar.getChildren().remove(2, vboxSonSonuclar.getChildren().size());
        }

        // Oynanmış maçları filtreleme
        List<Mac> oynananMaclar = new ArrayList<>();
        for (Mac m : tumMaclar) {
            if (m.isOynandiMi()) {
                oynananMaclar.add(m);
            }
        }

        // Son 3 maçı alma
        int toplamOynanan = oynananMaclar.size();
        List<Mac> son3 = new ArrayList<>();
        for (int i = Math.max(0, toplamOynanan - 3); i < toplamOynanan; i++) {
            son3.add(oynananMaclar.get(i));
        }
        
        // Tersten sıralama (En yeni en üstte)
        Collections.reverse(son3);

        for (Mac m : son3) {
            HBox satir = sonSonucSatiriOlustur(m);
            vboxSonSonuclar.getChildren().add(satir);
        }
        
        if (son3.isEmpty()) {
             Label bosLabel = new Label("Henüz oynanmış maç yok.");
             bosLabel.setStyle("-fx-text-fill: white; -fx-padding: 10;");
             vboxSonSonuclar.getChildren().add(bosLabel);
        }
    }

    // Yaklaşan maç satırı oluşturma
    private HBox yaklasanMacSatiriOlustur(Mac m) {
        HBox satir = new HBox();
        satir.setPrefHeight(39.0);
        
        Label lblEv = stilliEtiketOlustur(m.getEvSahibi().getAd());
        Label lblDep = stilliEtiketOlustur(m.getDeplasman().getAd());
        
        String tarihStr = "Tarih Yok";
        if (m.getMacTarihi() != null) {
            tarihStr = m.getMacTarihi().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
        Label lblTarih = stilliEtiketOlustur(tarihStr);

        satir.getChildren().addAll(lblEv, lblDep, lblTarih);
        return satir;
    }

    // Skor satırı oluşturma
    private HBox sonSonucSatiriOlustur(Mac m) {
        HBox satir = new HBox();
        satir.setPrefHeight(39.0);
        
        Label lblEv = stilliEtiketOlustur(m.getEvSahibi().getAd());
        Label lblSkor = stilliEtiketOlustur(m.getEvSahibiSkor() + " - " + m.getDeplasmanSkor());
        Label lblDep = stilliEtiketOlustur(m.getDeplasman().getAd());

        satir.getChildren().addAll(lblEv, lblSkor, lblDep);
        return satir;
    }

    // Stil verilmiş etiket oluşturma
    private Label stilliEtiketOlustur(String metin) {
        Label lbl = new Label(metin);
        lbl.setPrefWidth(250.0);
        lbl.setTextFill(javafx.scene.paint.Color.WHITE);
        lbl.setFont(new Font(14.0));
        lbl.setAlignment(javafx.geometry.Pos.CENTER);
        lbl.setPadding(new Insets(6, 5, 2, 5));
        return lbl;
    }
}
