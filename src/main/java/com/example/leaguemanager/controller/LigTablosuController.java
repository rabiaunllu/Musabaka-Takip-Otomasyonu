package com.example.leaguemanager.controller;

import com.example.leaguemanager.model.DataStore;
import com.example.leaguemanager.model.Mac;
import com.example.leaguemanager.model.Takim;
import com.example.leaguemanager.model.TakimIstatistik;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Puan durumu ekranı kontrolcüsü
public class LigTablosuController {

    @FXML private TableView<TakimIstatistik> tblLigTablosu;
    @FXML private TableColumn<TakimIstatistik, Integer> colSira;
    @FXML private TableColumn<TakimIstatistik, String> colTakim;
    @FXML private TableColumn<TakimIstatistik, Integer> colOynanan;
    @FXML private TableColumn<TakimIstatistik, Integer> colGalibiyet;
    @FXML private TableColumn<TakimIstatistik, Integer> colBeraberlik;
    @FXML private TableColumn<TakimIstatistik, Integer> colMaglubiyet;
    @FXML private TableColumn<TakimIstatistik, Integer> colAtilanGol;
    @FXML private TableColumn<TakimIstatistik, Integer> colYenenGol;
    @FXML private TableColumn<TakimIstatistik, Integer> colAveraj;
    @FXML private TableColumn<TakimIstatistik, Integer> colPuan;
    
    @FXML private javafx.scene.control.Button btnYenile;
    @FXML private javafx.scene.control.Button btnExcel;

    @FXML
    public void initialize() {
        // Tablo sütunlarını hazırlama
        tabloSutunlariniAyarla();
        // Puan durumunu hesapla
        puanDurumuHesapla();
        
        if (btnYenile != null) {
            btnYenile.setOnAction(event -> puanDurumuHesapla());
        }
    }

    private void tabloSutunlariniAyarla() {
        // Sıralama (1, 2, 3...) sütununu ayarlama
        colSira.setCellFactory(column -> {
            return new javafx.scene.control.TableCell<TakimIstatistik, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (this.getTableRow() != null && !empty) {
                        setText(String.valueOf(this.getTableRow().getIndex() + 1));
                    } else {
                        setText("");
                    }
                }
            };
        });

        // Diğer sütunları TakimIstatistik sınıfındaki alanlara bağlıyoruz
        colTakim.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTakim().getAd()));
        colOynanan.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getOynanan()).asObject());
        colGalibiyet.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getGalibiyet()).asObject());
        colBeraberlik.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getBeraberlik()).asObject());
        colMaglubiyet.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getMaglubiyet()).asObject());
        colAtilanGol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getAtilanGol()).asObject());
        colYenenGol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getYenenGol()).asObject());
        colAveraj.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getAveraj()).asObject());
        colPuan.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPuan()).asObject());
    }

    // Puan durumunu tek tek hesaplayan metod
    @FXML
    public void puanDurumuHesapla() {
        ObservableList<Takim> takımlar = DataStore.getInstance().takimlariGetir();
        ObservableList<Mac> maçlar = DataStore.getInstance().maclariGetir();

        Map<Takim, TakimIstatistik> istatistikHaritasi = new HashMap<>();

        // 1. Her takım için boş bir istatistik nesnesi oluşturuyoruz
        for (Takim t : takımlar) {
            if (!t.getAd().equals("BAY")) {
                istatistikHaritasi.put(t, new TakimIstatistik(t));
            }
        }

        // 2. Oynanan maçları tek tek dönüp skorları takımlara ekliyoruz
        for (Mac m : maçlar) {
            if (m.isOynandiMi()) {
                Takim ev = m.getEvSahibi();
                Takim dep = m.getDeplasman();

                TakimIstatistik evIstatistik = istatistikHaritasi.get(ev);
                TakimIstatistik depIstatistik = istatistikHaritasi.get(dep);

                int evSkor = m.getEvSahibiSkor();
                int depSkor = m.getDeplasmanSkor();

                // BAY maçlarını istatistiklere katma
                if (ev.getAd().equals("BAY") || dep.getAd().equals("BAY")) {
                    continue;
                }

                // Ev sahibi için istatistikleri işle
                if (evIstatistik != null) {
                    evIstatistik.setOynanan(evIstatistik.getOynanan() + 1);
                    evIstatistik.setAtilanGol(evIstatistik.getAtilanGol() + evSkor);
                    evIstatistik.setYenenGol(evIstatistik.getYenenGol() + depSkor);

                    if (evSkor > depSkor) {
                        evIstatistik.setGalibiyet(evIstatistik.getGalibiyet() + 1);
                    } else if (evSkor < depSkor) {
                        evIstatistik.setMaglubiyet(evIstatistik.getMaglubiyet() + 1);
                    } else {
                        evIstatistik.setBeraberlik(evIstatistik.getBeraberlik() + 1);
                    }
                }

                // Deplasman için istatistikleri işle
                if (depIstatistik != null) {
                    depIstatistik.setOynanan(depIstatistik.getOynanan() + 1);
                    depIstatistik.setAtilanGol(depIstatistik.getAtilanGol() + depSkor);
                    depIstatistik.setYenenGol(depIstatistik.getYenenGol() + evSkor);

                    if (depSkor > evSkor) {
                        depIstatistik.setGalibiyet(depIstatistik.getGalibiyet() + 1);
                    } else if (depSkor < evSkor) {
                        depIstatistik.setMaglubiyet(depIstatistik.getMaglubiyet() + 1);
                    } else {
                        depIstatistik.setBeraberlik(depIstatistik.getBeraberlik() + 1);
                    }
                }
            }
        }

        // 3. Sıralama yapıyoruz
        List<TakimIstatistik> liste = new ArrayList<>(istatistikHaritasi.values());
        
        liste.sort((s1, s2) -> {
            if (s2.getPuan() != s1.getPuan()) {
                return s2.getPuan() - s1.getPuan();
            }
            if (s2.getAveraj() != s1.getAveraj()) {
                return s2.getAveraj() - s1.getAveraj();
            }
            return s2.getAtilanGol() - s1.getAtilanGol();
        });

        // Tabloyu güncelle
        tblLigTablosu.setItems(FXCollections.observableArrayList(liste));
    }
    @FXML
    public void excelAktar() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Excel Dosyası Olarak Kaydet");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Excel Dosyaları", "*.xlsx"));
        fileChooser.setInitialFileName("LigTablosu.xlsx");
        
        // Hata yakalama için sahne kontrolü
        if (btnExcel == null || btnExcel.getScene() == null) {
            System.err.println("Excel butonu veya sahne null!");
            return;
        }

        java.io.File file = fileChooser.showSaveDialog(btnExcel.getScene().getWindow());
        
        if (file != null) {
            try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
                org.apache.poi.xssf.usermodel.XSSFSheet sheet = workbook.createSheet("Puan Durumu");
                
                // Başlık Satırı
                org.apache.poi.xssf.usermodel.XSSFRow headerRow = sheet.createRow(0);
                String[] columns = {"Sıra", "Takım", "O", "G", "B", "M", "AG", "YG", "AV", "P"};
                
                org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font font = workbook.createFont();
                font.setBold(true);
                headerStyle.setFont(font);
                
                for (int i = 0; i < columns.length; i++) {
                    org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columns[i]);
                    cell.setCellStyle(headerStyle);
                }
                
                // Verileri Yaz
                ObservableList<TakimIstatistik> items = tblLigTablosu.getItems();
                for (int i = 0; i < items.size(); i++) {
                    TakimIstatistik stat = items.get(i);
                    org.apache.poi.xssf.usermodel.XSSFRow row = sheet.createRow(i + 1);
                    
                    row.createCell(0).setCellValue(i + 1);
                    row.createCell(1).setCellValue(stat.getTakim().getAd());
                    row.createCell(2).setCellValue(stat.getOynanan());
                    row.createCell(3).setCellValue(stat.getGalibiyet());
                    row.createCell(4).setCellValue(stat.getBeraberlik());
                    row.createCell(5).setCellValue(stat.getMaglubiyet());
                    row.createCell(6).setCellValue(stat.getAtilanGol());
                    row.createCell(7).setCellValue(stat.getYenenGol());
                    row.createCell(8).setCellValue(stat.getAveraj());
                    row.createCell(9).setCellValue(stat.getPuan());
                }
                
                // Sütunları otomatik genişlet
                for (int i = 0; i < columns.length; i++) {
                    sheet.autoSizeColumn(i);
                }
                
                // Dosyayı kaydet
                try (java.io.FileOutputStream fileOut = new java.io.FileOutputStream(file)) {
                    workbook.write(fileOut);
                }
                
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Başarılı");
                alert.setContentText("Lig tablosu başarıyla Excel'e aktarıldı!");
                alert.showAndWait();
                
            } catch (Exception e) {
                e.printStackTrace();
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Hata");
                alert.setContentText("Excel dosyası oluşturulurken bir hata meydana geldi:\n" + e.getMessage());
                alert.showAndWait();
            }
        }
    }
}
