package com.example.leaguemanager.controller;

import com.example.leaguemanager.model.DataStore;
import com.example.leaguemanager.model.Match;
import com.example.leaguemanager.model.Team;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FixturesController {

    @FXML private ComboBox<String> weekSelector;
    @FXML private Label weekHeaderLabel;
    @FXML private TableView<Match> fixturesTable;
    @FXML private TableColumn<Match, String> homeTeamColumn;
    @FXML private TableColumn<Match, String> scoreColumn;
    @FXML private TableColumn<Match, String> awayTeamColumn;
    @FXML private TableColumn<Match, String> dateColumn;
    @FXML private Button createFixtureBtn;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @FXML
    public void initialize() {
        setupTableColumns();
        setupWeekSelector();

        // Otomatik yükleme kontrolü
        if (!DataStore.getInstance().getMatches().isEmpty()) {
            refreshWeekSelector();
            // İlk haftayı veya seçili haftayı yükle
            weekSelector.getSelectionModel().selectFirst();
            // Manuel tetikleme gerekebilir
            filterMatchesByWeek(weekSelector.getValue());
        }

        if (DataStore.getInstance().getCurrentUser() != null &&
            DataStore.getInstance().getCurrentUser().getRole() == com.example.leaguemanager.model.User.Role.USER) {
            createFixtureBtn.setVisible(false);
        }

        createFixtureBtn.setOnAction(event -> generateFixtures());
    }

    private void setupTableColumns() {
        homeTeamColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getHomeTeam().getName()));

        awayTeamColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getAwayTeam().getName()));

        scoreColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getScore()));
            
        dateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getMatchDate().format(dateFormatter)));

        // Actions column can be left empty for now or added later
    }

    private void setupWeekSelector() {
        weekSelector.setOnAction(event -> {
            String selectedWeek = weekSelector.getValue();
            if (selectedWeek != null) {
                filterMatchesByWeek(selectedWeek);
                weekHeaderLabel.setText(selectedWeek + " Fikstürü");
            }
        });
    }

    private void filterMatchesByWeek(String weekLabel) {
        if (weekLabel == null) return;
        
        // "Hafta 1" -> parse 1
        try {
            int weekNumber = Integer.parseInt(weekLabel.replace("Hafta ", ""));
            List<Match> filteredMatches = DataStore.getInstance().getMatches().stream()
                    .filter(m -> m.getWeek() == weekNumber)
                    .collect(Collectors.toList());
            fixturesTable.setItems(FXCollections.observableArrayList(filteredMatches));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void generateFixtures() {
        boolean success = DataStore.getInstance().generateFixedFixtures();
        
        if (!success) {
            showAlert("Yetersiz Takım", "Fikstür oluşturmak için en az 2 takım gereklidir.");
            return;
        }

        refreshWeekSelector();
        weekSelector.getSelectionModel().selectFirst(); // Hafta 1'i seç
        
        showAlert("Başarılı", "Fikstür başarıyla oluşturuldu.");
    }

    private void refreshWeekSelector() {
        ObservableList<Match> matches = DataStore.getInstance().getMatches();
        if (matches.isEmpty()) return;

        int maxWeek = matches.stream().mapToInt(Match::getWeek).max().orElse(0);
        
        List<String> weeks = new ArrayList<>();
        for (int i = 1; i <= maxWeek; i++) {
            weeks.add("Hafta " + i);
        }
        
        weekSelector.setItems(FXCollections.observableArrayList(weeks));
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
