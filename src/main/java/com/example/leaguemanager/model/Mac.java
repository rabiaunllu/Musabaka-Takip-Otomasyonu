package com.example.leaguemanager.model;

import java.time.LocalDateTime;
import java.io.Serializable;

// Maç bilgilerini tutan sınıf
public class Mac implements Serializable {
    private static final long serialVersionUID = 1L;

    private Takim evSahibi;      // Ev sahibi takım
    private Takim deplasman;     // Deplasman takımı
    private int evSahibiSkor;    // Ev sahibi skor
    private int deplasmanSkor;   // Deplasman skor
    private LocalDateTime macTarihi; // Maç tarihi
    private boolean oynandiMi;    // Maç oynandı mı?
    private int hafta;           // Kaçıncı hafta maçı?

    // Fikstür oluştururken kullanılan kurucu
    public Mac(Takim evSahibi, Takim deplasman, LocalDateTime macTarihi, int hafta) {
        this.evSahibi = evSahibi;
        this.deplasman = deplasman;
        this.macTarihi = macTarihi;
        this.hafta = hafta;
        this.oynandiMi = false;
        this.evSahibiSkor = 0;
        this.deplasmanSkor = 0;
    }

    // Tüm bilgileriyle maç oluşturmak için (dosyadan okurken vb.)
    public Mac(Takim evSahibi, Takim deplasman, int evSahibiSkor, int deplasmanSkor, LocalDateTime macTarihi, boolean oynandiMi, int hafta) {
        this.evSahibi = evSahibi;
        this.deplasman = deplasman;
        this.evSahibiSkor = evSahibiSkor;
        this.deplasmanSkor = deplasmanSkor;
        this.macTarihi = macTarihi;
        this.oynandiMi = oynandiMi;
        this.hafta = hafta;
    }

    // Getter ve Setterlar
    public Takim getEvSahibi() {
        return evSahibi;
    }

    public void setEvSahibi(Takim evSahibi) {
        this.evSahibi = evSahibi;
    }

    public Takim getDeplasman() {
        return deplasman;
    }

    public void setDeplasman(Takim deplasman) {
        this.deplasman = deplasman;
    }

    public int getEvSahibiSkor() {
        return evSahibiSkor;
    }

    public void setEvSahibiSkor(int evSahibiSkor) {
        this.evSahibiSkor = evSahibiSkor;
    }

    public int getDeplasmanSkor() {
        return deplasmanSkor;
    }

    public void setDeplasmanSkor(int deplasmanSkor) {
        this.deplasmanSkor = deplasmanSkor;
    }

    public LocalDateTime getMacTarihi() {
        return macTarihi;
    }

    public void setMacTarihi(LocalDateTime macTarihi) {
        this.macTarihi = macTarihi;
    }

    public boolean isOynandiMi() {
        return oynandiMi;
    }

    public void setOynandiMi(boolean oynandiMi) {
        this.oynandiMi = oynandiMi;
    }

    public int getHafta() {
        return hafta;
    }

    public void setHafta(int hafta) {
        this.hafta = hafta;
    }

    // Skoru string olarak döndüren yardımcı metod
    public String getSkor() {
        if (!oynandiMi) {
            return "- : -"; // Maç oynanmadıysa çizgi çekiyoruz
        }
        return evSahibiSkor + " - " + deplasmanSkor;
    }
}
