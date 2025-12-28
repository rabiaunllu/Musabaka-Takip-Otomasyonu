package com.example.leaguemanager.controller;

import com.example.leaguemanager.model.DataStore;
import com.example.leaguemanager.model.Match;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardController {

    @FXML private Label totalTeamsLabel;
    @FXML private Label totalMatchesLabel;
    @FXML private Label currentWeekLabel;
    @FXML private VBox upcomingMatchesContainer;
    @FXML private VBox recentResultsContainer;

    @FXML
    public void initialize() {
        refreshData();
    }

    public void refreshData() {
        DataStore store = DataStore.getInstance();

        // 1. Stats Cards
        totalTeamsLabel.setText(String.valueOf(store.getTeams().size()));
        totalMatchesLabel.setText(String.valueOf(store.getMatches().size()));
        
        // Calculate Current Week (Max week of played matches + 1, or 1)
        int maxPlayedWeek = store.getMatches().stream()
                .filter(Match::isPlayed)
                .mapToInt(Match::getWeek)
                .max()
                .orElse(0); 
        currentWeekLabel.setText(String.valueOf(maxPlayedWeek + 1));

        // 2. Upcoming Matches Tables
        populateUpcomingMatches(store.getMatches());

        // 3. Recent Results Tables
        populateRecentResults(store.getMatches());
    }

    private void populateUpcomingMatches(List<Match> allMatches) {
        // Clear existing rows (keep header, usually index 0 and 1 are labels/headers, verify in FXML structure)
        // In our FXML, index 0 is Label (Header), index 1 is HBox (Column Header). So we keep first 2 children.
        if (upcomingMatchesContainer.getChildren().size() > 2) {
            upcomingMatchesContainer.getChildren().remove(2, upcomingMatchesContainer.getChildren().size());
        }

        List<Match> upcoming = allMatches.stream()
                .filter(m -> !m.isPlayed())
                .limit(3)
                .collect(Collectors.toList());

        for (Match m : upcoming) {
            HBox row = createUpcomingMatchRow(m);
            upcomingMatchesContainer.getChildren().add(row);
        }
        
        if (upcoming.isEmpty()) {
             Label emptyLbl = new Label("Yaklaşan maç bulunamadı.");
             emptyLbl.setStyle("-fx-text-fill: white; -fx-padding: 10;");
             upcomingMatchesContainer.getChildren().add(emptyLbl);
        }
    }

    private void populateRecentResults(List<Match> allMatches) {
        if (recentResultsContainer.getChildren().size() > 2) {
            recentResultsContainer.getChildren().remove(2, recentResultsContainer.getChildren().size());
        }

        List<Match> playedMatches = allMatches.stream().filter(Match::isPlayed).collect(Collectors.toList());
        // Get last 3
        int start = Math.max(0, playedMatches.size() - 3);
        List<Match> last3 = playedMatches.subList(start, playedMatches.size());
        // Reverse them to show newest first
        java.util.Collections.reverse(last3);

        for (Match m : last3) {
            HBox row = createRecentResultRow(m);
            recentResultsContainer.getChildren().add(row);
        }
        
        if (last3.isEmpty()) {
             Label emptyLbl = new Label("Henüz oynanmış maç yok.");
             emptyLbl.setStyle("-fx-text-fill: white; -fx-padding: 10;");
             recentResultsContainer.getChildren().add(emptyLbl);
        }
    }

    private HBox createUpcomingMatchRow(Match m) {
        HBox row = new HBox();
        row.setPrefHeight(39.0);
        row.setPrefWidth(838.0); // Or use HBox.hgrow
        
        Label lblHome = createStyledLabel(m.getHomeTeam().getName());
        Label lblAway = createStyledLabel(m.getAwayTeam().getName());
        
        String dateStr = (m.getMatchDate() != null) 
            ? m.getMatchDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) 
            : "Tarih Yok";
        Label lblDate = createStyledLabel(dateStr);

        row.getChildren().addAll(lblHome, lblAway, lblDate);
        return row;
    }

    private HBox createRecentResultRow(Match m) {
        HBox row = new HBox();
        row.setPrefHeight(39.0);
        
        Label lblHome = createStyledLabel(m.getHomeTeam().getName());
        Label lblScore = createStyledLabel(m.getHomeScore() + " - " + m.getAwayScore());
        Label lblAway = createStyledLabel(m.getAwayTeam().getName());

        row.getChildren().addAll(lblHome, lblScore, lblAway);
        return row;
    }

    private Label createStyledLabel(String text) {
        Label lbl = new Label(text);
        lbl.setPrefWidth(250.0);
        lbl.setTextFill(javafx.scene.paint.Color.WHITE);
        lbl.setFont(new Font(14.0));
        lbl.setPadding(new Insets(6, 5, 2, 15)); // top, right, bottom, left
        return lbl;
    }
}
