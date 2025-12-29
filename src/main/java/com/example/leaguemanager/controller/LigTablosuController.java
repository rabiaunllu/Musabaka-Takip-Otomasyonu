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
}
