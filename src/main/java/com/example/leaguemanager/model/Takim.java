package com.example.leaguemanager.model;

import java.io.Serializable;

// Takım bilgilerini tutan sınıf
public class Takim implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ad;      // Takım adı
    private String sehir;   // Şehir
    private String stadyum; // Stadyum adı

    // Kurucu metod (Constructor)
    public Takim(String ad, String sehir, String stadyum) {
        this.ad = ad;
        this.sehir = sehir;
        this.stadyum = stadyum;
    }

    // Getter ve Setter metodları
    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getSehir() {
        return sehir;
    }

    public void setSehir(String sehir) {
        this.sehir = sehir;
    }

    public String getStadyum() {
        return stadyum;
    }

    public void setStadyum(String stadyum) {
        this.stadyum = stadyum;
    }

    // Listelerde düzgün görünmesi için ad döndürüyoruz
    @Override
    public String toString() {
        return ad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Takim takim = (Takim) o;
        return ad != null ? ad.equals(takim.ad) : takim.ad == null;
    }

    @Override
    public int hashCode() {
        return ad != null ? ad.hashCode() : 0;
    }
}
