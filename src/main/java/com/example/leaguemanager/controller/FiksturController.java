package com.example.leaguemanager.controller;

import com.example.leaguemanager.model.DataStore;
import com.example.leaguemanager.model.Kullanici;
import com.example.leaguemanager.model.Mac;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Fikstür ekranı kontrolcüsü
public class FiksturController extends BaseController {

    @FXML private ComboBox<String> cmbHaftaSecici; // Hafta seçme kutusu
    @FXML private Label lblHaftaBasligi;          // Hangi hafta olduğunu gösteren başlık
    @FXML private TableView<Mac> tblFikstur;      // Maçların tablosu
    @FXML private TableColumn<Mac, String> colEvSahibi;
    @FXML private TableColumn<Mac, String> colSkor;
    @FXML private TableColumn<Mac, String> colDeplasman;
    @FXML private TableColumn<Mac, String> colTarih;
    @FXML private Button btnFiksturOlustur;       // Fikstür oluştur butonu

    // Tarih formatı
    private final DateTimeFormatter tarihFormatlayici = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @FXML
    public void initialize() {
        // Tablo sütunlarını hazırlama
        tabloSutunlariniAyarla();
        
        // Hafta seçici kutuyu hazırlama
        haftaSeciciAyarla();

        // Eğer sistemde maçlar varsa listeyi doldur
        if (!DataStore.getInstance().maclariGetir().isEmpty()) {
            haftaSeciciYenile();
            cmbHaftaSecici.getSelectionModel().selectFirst();
            maclariHaftayaGoreFiltrele(cmbHaftaSecici.getValue());
        }

        // Admin değilse fikstür oluşturma butonunu gizle
        if (DataStore.getInstance().mevcutKullaniciyiGetir() != null &&
            DataStore.getInstance().mevcutKullaniciyiGetir().getRol() == Kullanici.Role.USER) {
            btnFiksturOlustur.setVisible(false);
        }

        // Butona tıklanınca fikstür oluşturma metodunu çağır
        btnFiksturOlustur.setOnAction(event -> fiksturOlustur());
    }

    // Tablo sütunlarını ayarlar
    private void tabloSutunlariniAyarla() {
        colEvSahibi.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getEvSahibi().getAd()));

        colDeplasman.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDeplasman().getAd()));

        colSkor.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getSkor()));
            
        colTarih.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getMacTarihi().format(tarihFormatlayici)));
    }

    // Hafta seçildiğinde olacak işlemleri ayarlar
    private void haftaSeciciAyarla() {
        cmbHaftaSecici.setOnAction(event -> {
            String seciliHafta = cmbHaftaSecici.getValue();
            if (seciliHafta != null) {
                maclariHaftayaGoreFiltrele(seciliHafta);
                lblHaftaBasligi.setText(seciliHafta + " Fikstürü");
            }
        });
    }

    // Seçilen haftaya göre maçları filtreleyen metod
    private void maclariHaftayaGoreFiltrele(String haftaEtiketi) {
        if (haftaEtiketi == null) return;
        
        try {
            // "Hafta 1" yazısından "1" sayısını alıyoruz
            int haftaNo = Integer.parseInt(haftaEtiketi.replace("Hafta ", ""));
            
            List<Mac> filtrelenmişMaclar = new ArrayList<>();
            for (Mac m : DataStore.getInstance().maclariGetir()) {
                if (m.getHafta() == haftaNo) {
                    filtrelenmişMaclar.add(m);
                }
            }
            
            tblFikstur.setItems(FXCollections.observableArrayList(filtrelenmişMaclar));
        } catch (Exception e) {
            System.out.println("Filtreleme hatası!");
        }
    }

    // Fikstür oluşturma butonu metodu
    private void fiksturOlustur() {
        boolean basarili = DataStore.getInstance().fiksturOlustur();
        
        if (!basarili) {
            uyariGoster("Hata", "Fikstür oluşturmak için en az 2 takım lazım.");
            return;
        }

        haftaSeciciYenile();
        cmbHaftaSecici.getSelectionModel().selectFirst();
        
        uyariGoster("Başarılı", "Fikstür başarıyla oluşturuldu.");
    }

    // Hafta seçim kutusunu günceller
    private void haftaSeciciYenile() {
        ObservableList<Mac> tumMaclar = DataStore.getInstance().maclariGetir();
        if (tumMaclar.isEmpty()) return;

        int enBuyukHafta = 0;
        for (Mac m : tumMaclar) {
            if (m.getHafta() > enBuyukHafta) {
                enBuyukHafta = m.getHafta();
            }
        }
        
        List<String> haftaListesi = new ArrayList<>();
        for (int i = 1; i <= enBuyukHafta; i++) {
            haftaListesi.add("Hafta " + i);
        }
        
        cmbHaftaSecici.setItems(FXCollections.observableArrayList(haftaListesi));
    }


}
