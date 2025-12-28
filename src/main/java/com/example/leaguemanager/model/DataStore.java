package com.example.leaguemanager.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataStore {
    private static DataStore instance;

    private ObservableList<Team> teams;
    private ObservableList<Match> matches;

    private DataStore() {
        teams = FXCollections.observableArrayList();
        matches = FXCollections.observableArrayList();
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    public ObservableList<Team> getTeams() {
        return teams;
    }

    public ObservableList<Match> getMatches() {
        return matches;
    }

    public void loadDummyData() {
        teams.clear();
        teams.add(new Team("Galatasaray", "İstanbul", "RAMS Park"));
        teams.add(new Team("Fenerbahçe", "İstanbul", "Ülker Stadyumu"));
        teams.add(new Team("Beşiktaş", "İstanbul", "Tüpraş Stadyumu"));
        teams.add(new Team("Trabzonspor", "Trabzon", "Papara Park"));
        teams.add(new Team("Başakşehir", "İstanbul", "Başakşehir Fatih Terim"));
        teams.add(new Team("Adana Demirspor", "Adana", "Yeni Adana Stadyumu"));
        
        System.out.println("Dummy data loaded: " + teams.size() + " teams.");
    }
}
