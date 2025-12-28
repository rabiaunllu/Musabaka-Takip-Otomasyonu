package com.example.leaguemanager.model;

import java.io.Serializable;

public class Kullanici implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Role {
        ADMIN,
        DEVELOPER,
        USER
    }

    private String kullaniciAdi;
    private String sifre;
    private Role rol;

    public Kullanici(String kullaniciAdi, String sifre, Role rol) {
        this.kullaniciAdi = kullaniciAdi;
        this.sifre = sifre;
        this.rol = rol;
    }

    public String getKullaniciAdi() {
        return kullaniciAdi;
    }

    public String getSifre() {
        return sifre;
    }

    public Role getRol() {
        return rol;
    }

    @Override
    public String toString() {
        return kullaniciAdi + " (" + rol + ")";
    }
}
