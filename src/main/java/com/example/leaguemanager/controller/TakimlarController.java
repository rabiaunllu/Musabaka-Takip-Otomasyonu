package com.example.leaguemanager.controller;

import com.example.leaguemanager.model.DataStore;
import com.example.leaguemanager.model.Takim;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

// Takım yönetim ekranının kontrolcüsü
public class TakimlarController {

    @FXML private TableView<Takim> tblTakimlar;
    @FXML private TableColumn<Takim, String> colAd;
    @FXML private TableColumn<Takim, String> colSehir;
    @FXML private TableColumn<Takim, String> colStadyum;

    @FXML private TextField txtAd;
    @FXML private TextField txtSehir;
    @FXML private TextField txtStadyum;

    @FXML private Button btnEkle;
    @FXML private Button btnSil;
    @FXML private Button btnFiksturOlustur;
    @FXML private VBox detayPaneli;

    @FXML
    public void initialize() {
        // Tablo sütunlarını bağlama (Takim sınıfındaki yeni alan isimlerine göre)
        colAd.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAd()));
        colSehir.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSehir()));
        colStadyum.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStadyum()));

        // Verileri DataStore'dan çekip tabloya bağlama
        tblTakimlar.setItems(DataStore.getInstance().takimlariGetir());

        // Eğer liste boşsa test verilerini yükle
        if (DataStore.getInstance().takimlariGetir().isEmpty()) {
            DataStore.getInstance().ornekVerileriYukle();
        }

        // Tablodan bir takım seçildiğinde bilgilerini kutucuklara doldur
        tblTakimlar.getSelectionModel().selectedItemProperty().addListener((obs, eskiSecim, yeniSecim) -> {
            if (yeniSecim != null) {
                txtAd.setText(yeniSecim.getAd());
                txtSehir.setText(yeniSecim.getSehir());
                txtStadyum.setText(yeniSecim.getStadyum());
            }
        });

        // Kullanıcı rolüne göre bazı özellikleri gizle
        if (DataStore.getInstance().mevcutKullaniciyiGetir() != null &&
            DataStore.getInstance().mevcutKullaniciyiGetir().getRol().name().equals("USER")) {
            detayPaneli.setVisible(false);
            detayPaneli.setManaged(false);
            btnFiksturOlustur.setVisible(false);
            btnFiksturOlustur.setManaged(false);
        }
    }

    // Takım ekleme butonu
    @FXML
    private void takimEkle() {
        String ad = txtAd.getText().trim();
        String sehir = txtSehir.getText().trim();
        String stadyum = txtStadyum.getText().trim();

        // Boş alan kontrolü
        if (ad.isEmpty() || sehir.isEmpty() || stadyum.isEmpty()) {
            uyariGoster("Hata", "Lütfen tüm kutucukları doldurun!");
            return;
        }

        // Karakter Sınırı Kontrolü
        if (ad.length() > 30) {
            uyariGoster("Uyarı", "Takım adı en fazla 30 karakter olabilir!");
            return;
        }
        if (sehir.length() > 30) {
            uyariGoster("Uyarı", "Şehir adı en fazla 30 karakter olabilir!");
            return;
        }
        // Şehir adında rakam kontrolü
        if (sehir.matches(".*\\d.*")) {
            uyariGoster("Uyarı", "Şehir adında rakam bulunamaz!");
            return;
        }
        if (stadyum.length() > 50) {
            uyariGoster("Uyarı", "Stadyum adı en fazla 50 karakter olabilir!");
            return;
        }

        // Aynı isimde takım kontrolü & "BAY" ismi kontrolü
        if (ad.equalsIgnoreCase("BAY")) {
            uyariGoster("Hata", "'BAY' ismi sistem tarafından ayrılmıştır, kullanılamaz.");
            return;
        }

        for (Takim t : DataStore.getInstance().takimlariGetir()) {
            if (t.getAd().equalsIgnoreCase(ad)) {
                uyariGoster("Hata", "Bu isimde bir takım zaten mevcut!");
                return;
            }
        }

        // Yeni takımı listeye ekle ve kaydet
        Takim yeniTakim = new Takim(ad, sehir, stadyum);
        DataStore.getInstance().takimlariGetir().add(yeniTakim);
        DataStore.getInstance().veriyiKaydet();
        
        // Kutucukları temizle
        alanlariTemizle();
    }

    // Takım silme butonu
    @FXML
    private void takimSil() {
        Takim seciliTakim = tblTakimlar.getSelectionModel().getSelectedItem();
        if (seciliTakim != null) {
            DataStore.getInstance().takimlariGetir().remove(seciliTakim);
            DataStore.getInstance().veriyiKaydet();
            alanlariTemizle();
        } else {
            uyariGoster("Uyarı", "Lütfen silmek için önce bir takım seçin.");
        }
    }

    // Fikstür oluştur butonu
    @FXML
    private void fiksturOlustur() {
        boolean basarili = DataStore.getInstance().fiksturOlustur();
        
        if (basarili) {
            uyariGoster("Bilgi", "Fikstür başarıyla oluşturuldu.");
        } else {
             uyariGoster("Uyarı", "Yeterli takım yok, fikstür oluşturulamadı!");
        }

        // Fikstür ekranına geçiş yap
        if (MainController.getOrnek() != null) {
            MainController.getOrnek().fiksturuGoster();
        }
    }

    // Kutucukları temizleyen yardımcı metod
    private void alanlariTemizle() {
        txtAd.clear();
        txtSehir.clear();
        txtStadyum.clear();
    }

    // Uyarı mesajı gösteren metod
    private void uyariGoster(String baslik, String icerik) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(baslik);
        alert.setHeaderText(null);
        alert.setContentText(icerik);
        alert.showAndWait();
    }
}
