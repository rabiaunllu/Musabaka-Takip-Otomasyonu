package com.example.leaguemanager.model;

public class TakimIstatistik {
    private Takim takim;
    private int oynanan;
    private int galibiyet;
    private int beraberlik;
    private int maglubiyet;
    private int atilanGol;
    private int yenenGol;

    public TakimIstatistik(Takim takim) {
        this.takim = takim;
        this.oynanan = 0;
        this.galibiyet = 0;
        this.beraberlik = 0;
        this.maglubiyet = 0;
        this.atilanGol = 0;
        this.yenenGol = 0;
    }

    public Takim getTakim() {
        return takim;
    }

    public void setTakim(Takim takim) {
        this.takim = takim;
    }

    public int getOynanan() {
        return oynanan;
    }

    public void setOynanan(int oynanan) {
        this.oynanan = oynanan;
    }

    public int getGalibiyet() {
        return galibiyet;
    }

    public void setGalibiyet(int galibiyet) {
        this.galibiyet = galibiyet;
    }

    public int getBeraberlik() {
        return beraberlik;
    }

    public void setBeraberlik(int beraberlik) {
        this.beraberlik = beraberlik;
    }

    public int getMaglubiyet() {
        return maglubiyet;
    }

    public void setMaglubiyet(int maglubiyet) {
        this.maglubiyet = maglubiyet;
    }

    public int getAtilanGol() {
        return atilanGol;
    }

    public void setAtilanGol(int atilanGol) {
        this.atilanGol = atilanGol;
    }

    public int getYenenGol() {
        return yenenGol;
    }

    public void setYenenGol(int yenenGol) {
        this.yenenGol = yenenGol;
    }

    public int getAveraj() {
        return atilanGol - yenenGol;
    }

    public int getPuan() {
        return (galibiyet * 3) + (beraberlik * 1);
    }
}
