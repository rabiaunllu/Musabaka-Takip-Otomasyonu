package com.example.leaguemanager.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;

public class DataStore {
    private static DataStore instance;

    private ObservableList<Team> teams;
    private ObservableList<Match> matches;

    private static final String DATA_FILE = "league_data.dat";

    private DataStore() {
        teams = FXCollections.observableArrayList();
        matches = FXCollections.observableArrayList();
        loadData(); // Load data on startup
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

    // --- Persistence Methods ---

    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            // ObservableList is not Serializable, convert to ArrayList
            oos.writeObject(new java.util.ArrayList<>(teams));
            oos.writeObject(new java.util.ArrayList<>(matches));
            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.out.println("Data file not found. Starting with empty data.");
            // Optional: loadDummyData(); if you want defaults instead of empty
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            java.util.List<Team> loadedTeams = (java.util.List<Team>) ois.readObject();
            java.util.List<Match> loadedMatches = (java.util.List<Match>) ois.readObject();

            teams.setAll(loadedTeams);
            matches.setAll(loadedMatches);
            System.out.println("Data loaded successfully: " + teams.size() + " teams, " + matches.size() + " matches.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Error loading data: " + e.getMessage());
        }
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
        saveData(); // Save after loading dummy
    }

    public boolean generateFixedFixtures() {
        if (teams.isEmpty() || teams.size() < 2) {
            return false;
        }

        matches.clear();
        java.util.List<Team> teamList = new java.util.ArrayList<>(teams);
        
        // Takım sayısı tek ise BAY (Dummy) takımı ekle
        if (teamList.size() % 2 != 0) {
            teamList.add(new Team("BAY", "-", "-"));
        }

        int numTeams = teamList.size();
        int numRounds = (numTeams - 1) * 2; // Çift devre
        int matchesPerRound = numTeams / 2;
        java.util.List<Match> allMatches = new java.util.ArrayList<>();
        
        // Round Robin Algoritması
        // İlk yarı
        for (int round = 0; round < numTeams - 1; round++) {
            for (int matchIdx = 0; matchIdx < matchesPerRound; matchIdx++) {
                int homeIdx = (round + matchIdx) % (numTeams - 1);
                int awayIdx = (numTeams - 1 - matchIdx + round) % (numTeams - 1);

                if (matchIdx == 0) {
                    awayIdx = numTeams - 1;
                }

                Team home = teamList.get(homeIdx);
                Team away = teamList.get(awayIdx);

                if (matchIdx == 0 && round % 2 == 1) {
                    Team temp = home;
                    home = away;
                    away = temp;
                }

                int currentWeek = round + 1;
                java.time.LocalDateTime matchDate = java.time.LocalDateTime.now().plusDays((currentWeek - 1) * 7);
                allMatches.add(new Match(home, away, matchDate, currentWeek));
            }
        }

        // İkinci yarı (Rövanş)
        for (int round = 0; round < numTeams - 1; round++) {
            for (int matchIdx = 0; matchIdx < matchesPerRound; matchIdx++) {
                int homeIdx = (round + matchIdx) % (numTeams - 1);
                int awayIdx = (numTeams - 1 - matchIdx + round) % (numTeams - 1);

                if (matchIdx == 0) {
                    awayIdx = numTeams - 1;
                }

                Team home = teamList.get(homeIdx);
                Team away = teamList.get(awayIdx);

                if (matchIdx == 0 && round % 2 == 1) {
                    Team temp = home;
                    home = away;
                    away = temp;
                }

                int secondHalfWeek = (round + 1) + (numTeams - 1);
                java.time.LocalDateTime matchDate = java.time.LocalDateTime.now().plusDays((secondHalfWeek - 1) * 7);
                // Rövanş: Ev sahibi ve deplasman yer değiştirir
                allMatches.add(new Match(away, home, matchDate, secondHalfWeek));
            }
        }

        matches.addAll(allMatches);
        saveData(); // Save generated fixtures
        return true;
    }
}
