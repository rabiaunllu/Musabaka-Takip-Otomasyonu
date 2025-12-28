package com.example.leaguemanager.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Verilerin saklandığı ve yönetildiği sınıf (Veri Deposu)
public class DataStore {
    // Singleton deseni
    private static DataStore ornek;

    private ObservableList<Takim> takimlar;
    private ObservableList<Mac> maclar;
    private ObservableList<Kullanici> kullanicilar;
    private Kullanici mevcutKullanici;

    private static final String VERI_DOSYASI = "league_data.dat";
    private static final String OTURUM_DOSYASI = "session.dat";

    private DataStore() {
        takimlar = FXCollections.observableArrayList();
        maclar = FXCollections.observableArrayList();
        kullanicilar = FXCollections.observableArrayList();
        
        veriyiYukle();
        
        if (kullanicilar.isEmpty()) {
            kullanicilar.add(new Kullanici("admin", "123", Kullanici.Role.ADMIN));
            kullanicilar.add(new Kullanici("dev", "123", Kullanici.Role.DEVELOPER));
            kullanicilar.add(new Kullanici("user", "123", Kullanici.Role.USER));
            veriyiKaydet();
        }
    }

    public static DataStore getInstance() {
        if (ornek == null) {
            ornek = new DataStore();
        }
        return ornek;
    }

    // Getter ve Setterlar
    public ObservableList<Kullanici> kullanicilariGetir() {
        return kullanicilar;
    }

    public ObservableList<Takim> takimlariGetir() {
        return takimlar;
    }

    public ObservableList<Mac> maclariGetir() {
        return maclar;
    }

    public Kullanici mevcutKullaniciyiGetir() {
        return mevcutKullanici;
    }

    public void mevcutKullaniciAyarla(Kullanici kullanici) {
        this.mevcutKullanici = kullanici;
    }

    // --- Dosya İşlemleri ---

    public void veriyiKaydet() {
        try {
            FileOutputStream fos = new FileOutputStream(VERI_DOSYASI);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            
            oos.writeObject(new ArrayList<>(takimlar));
            oos.writeObject(new ArrayList<>(maclar));
            oos.writeObject(new ArrayList<>(kullanicilar));
            
            oos.close();
        } catch (IOException e) {
            System.out.println("Veri kaydetme hatası: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void veriyiYukle() {
        File dosya = new File(VERI_DOSYASI);
        if (!dosya.exists()) return;

        try {
            FileInputStream fis = new FileInputStream(dosya);
            ObjectInputStream ois = new ObjectInputStream(fis);
            
            List<Takim> yuklenenTakimlar = (List<Takim>) ois.readObject();
            List<Mac> yuklenenMaclar = (List<Mac>) ois.readObject();
            
            try {
                List<Kullanici> yuklenenKullanicilar = (List<Kullanici>) ois.readObject();
                kullanicilar.setAll(yuklenenKullanicilar);
            } catch (Exception e) {
                System.out.println("Eski veri dosyası.");
            }

            takimlar.setAll(yuklenenTakimlar);
            maclar.setAll(yuklenenMaclar);
            
            ois.close();
        } catch (Exception e) {
            System.out.println("Veri yükleme hatası.");
        }
    }

    // --- Oturum İşlemleri ---

    public void oturumuKaydet(Kullanici kullanici) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(OTURUM_DOSYASI));
            oos.writeObject(kullanici);
            oos.close();
        } catch (IOException e) {
            System.out.println("Oturum kaydedilemedi.");
        }
    }

    public Kullanici oturumuYukle() {
        File dosya = new File(OTURUM_DOSYASI);
        if (!dosya.exists()) return null;

        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dosya));
            Kullanici kullanici = (Kullanici) ois.readObject();
            ois.close();

            if (kullanici != null) {
                for (Kullanici u : kullanicilar) {
                    if (u.getKullaniciAdi().equals(kullanici.getKullaniciAdi())) {
                        this.mevcutKullanici = kullanici;
                        return kullanici;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Oturum yüklenemedi.");
        }
        return null;
    }

    public void cikisYap() {
        this.mevcutKullanici = null;
        File dosya = new File(OTURUM_DOSYASI);
        if (dosya.exists()) {
            dosya.delete();
        }
    }

    public void ornekVerileriYukle() {
        takimlar.clear();
        takimlar.add(new Takim("Galatasaray", "İstanbul", "RAMS Park"));
        takimlar.add(new Takim("Fenerbahçe", "İstanbul", "Ülker Stadyumu"));
        takimlar.add(new Takim("Beşiktaş", "İstanbul", "Tüpraş Stadyumu"));
        takimlar.add(new Takim("Trabzonspor", "Trabzon", "Papara Park"));
        takimlar.add(new Takim("Başakşehir", "İstanbul", "Başakşehir Fatih Terim"));
        takimlar.add(new Takim("Adana Demirspor", "Adana", "Yeni Adana Stadyumu"));
        
        veriyiKaydet();
    }

    public boolean fiksturOlustur() {
        if (takimlar.isEmpty() || takimlar.size() < 2) {
            return false;
        }

        maclar.clear();
        List<Takim> takimList = new ArrayList<>(takimlar);
        
        if (takimList.size() % 2 != 0) {
            takimList.add(new Takim("BAY", "-", "-"));
        }

        int n = takimList.size();
        int macSayisiHaftalik = n / 2;
        
        // İlk yarı fikstürü
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < macSayisiHaftalik; j++) {
                int ev = (i + j) % (n - 1);
                int dep = (n - 1 - j + i) % (n - 1);

                if (j == 0) {
                    dep = n - 1;
                }

                Takim evSahibi = takimList.get(ev);
                Takim deplasman = takimList.get(dep);

                if (j == 0 && i % 2 == 1) {
                    Takim gecici = evSahibi;
                    evSahibi = deplasman;
                    deplasman = gecici;
                }

                int hafta = i + 1;
                java.time.LocalDateTime tarih = java.time.LocalDateTime.now().plusDays((hafta - 1) * 7);
                maclar.add(new Mac(evSahibi, deplasman, tarih, hafta));
            }
        }

        // İkinci yarı fikstürü
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < macSayisiHaftalik; j++) {
                int ev = (i + j) % (n - 1);
                int dep = (n - 1 - j + i) % (n - 1);

                if (j == 0) {
                    dep = n - 1;
                }

                Takim evSahibi = takimList.get(ev);
                Takim deplasman = takimList.get(dep);

                if (j == 0 && i % 2 == 1) {
                    Takim gecici = evSahibi;
                    evSahibi = deplasman;
                    deplasman = gecici;
                }

                int hafta = (i + 1) + (n - 1);
                java.time.LocalDateTime tarih = java.time.LocalDateTime.now().plusDays((hafta - 1) * 7);
                maclar.add(new Mac(deplasman, evSahibi, tarih, hafta));
            }
        }

        veriyiKaydet();
        return true;
    }
}
