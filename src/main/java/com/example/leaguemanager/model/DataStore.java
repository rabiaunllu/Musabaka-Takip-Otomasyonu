package com.example.leaguemanager.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;

public class DataStore {
    private static DataStore instance;

    private ObservableList<Team> teams;
    private ObservableList<Match> matches;

    private static final String DATA_FILE = "league_data.dat";

    private ObservableList<User> users;
    private User currentUser;
    private static final String SESSION_FILE = "session.dat";

    // ... existing constructor ...
    private DataStore() {
        teams = FXCollections.observableArrayList();
        matches = FXCollections.observableArrayList();
        users = FXCollections.observableArrayList();
        
        loadData(); // Load data
        
        // Ensure default users exist if empty
        if (users.isEmpty()) {
            users.add(new User("admin", "123", User.Role.ADMIN));
            users.add(new User("dev", "123", User.Role.DEVELOPER));
            users.add(new User("user", "123", User.Role.USER));
            saveData(); // Save new users
        }
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    public ObservableList<User> getUsers() {
        return users;
    }

    public ObservableList<Team> getTeams() {
        return teams;
    }

    public ObservableList<Match> getMatches() {
        return matches;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    // --- Persistence Methods ---

    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(new java.util.ArrayList<>(teams));
            oos.writeObject(new java.util.ArrayList<>(matches));
            oos.writeObject(new java.util.ArrayList<>(users)); // Save users too
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
            System.out.println("Data file not found. Starting with empty/default data.");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            java.util.List<Team> loadedTeams = (java.util.List<Team>) ois.readObject();
            java.util.List<Match> loadedMatches = (java.util.List<Match>) ois.readObject();
            
            // Try reading users, handle legacy files without users
            try {
                java.util.List<User> loadedUsers = (java.util.List<User>) ois.readObject();
                users.setAll(loadedUsers);
            } catch (EOFException | OptionalDataException e) {
                System.out.println("Legacy data file detected (no users). Defaults will be created.");
            }

            teams.setAll(loadedTeams);
            matches.setAll(loadedMatches);
            System.out.println("Data loaded.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // --- Session Management ---

    public void saveSession(User user) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SESSION_FILE))) {
            oos.writeObject(user);
            System.out.println("Session saved for: " + user.getUsername());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User loadSession() {
        File file = new File(SESSION_FILE);
        if (!file.exists()) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            User user = (User) ois.readObject();
            if (user != null) {
                // Validate if user still exists in our db (optional but good practice)
                boolean exists = users.stream().anyMatch(u -> u.getUsername().equals(user.getUsername()));
                if (exists) {
                    this.currentUser = user;
                    return user;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            // Invalid session or file error
            System.out.println("Session load failed");
        }
        return null;
    }

    public void logout() {
        this.currentUser = null;
        File file = new File(SESSION_FILE);
        if (file.exists()) {
            file.delete();
        }
        System.out.println("Logged out.");
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
