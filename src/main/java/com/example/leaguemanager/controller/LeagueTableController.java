package com.example.leaguemanager.controller;

import com.example.leaguemanager.model.DataStore;
import com.example.leaguemanager.model.Match;
import com.example.leaguemanager.model.Team;
import com.example.leaguemanager.model.TeamStats;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LeagueTableController {

    @FXML private TableView<TeamStats> leagueTable;
    @FXML private TableColumn<TeamStats, Integer> posColumn;
    @FXML private TableColumn<TeamStats, String> teamColumn;
    @FXML private TableColumn<TeamStats, Integer> playedColumn;
    @FXML private TableColumn<TeamStats, Integer> wonColumn;
    @FXML private TableColumn<TeamStats, Integer> drawColumn;
    @FXML private TableColumn<TeamStats, Integer> lostColumn;
    @FXML private TableColumn<TeamStats, Integer> gfColumn;
    @FXML private TableColumn<TeamStats, Integer> gaColumn;
    @FXML private TableColumn<TeamStats, Integer> gdColumn;
    @FXML private TableColumn<TeamStats, Integer> pointsColumn;

    @FXML private javafx.scene.control.Button refreshButton;

    @FXML
    public void initialize() {
        setupTableColumns();
        calculateStandings();
        
        if (refreshButton != null) {
            refreshButton.setOnAction(event -> calculateStandings());
        }
    }

    private void setupTableColumns() {
        // Sıralama (Position)
        posColumn.setCellFactory(column -> {
            return new javafx.scene.control.TableCell<TeamStats, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (this.getTableRow() != null && !empty) {
                        setText(String.valueOf(this.getTableRow().getIndex() + 1));
                    } else {
                        setText("");
                    }
                }
            };
        });

        teamColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTeam().getName()));
        playedColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPlayed()).asObject());
        wonColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getWon()).asObject());
        drawColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDrawn()).asObject());
        lostColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getLost()).asObject());
        gfColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getGoalsFor()).asObject());
        gaColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getGoalsAgainst()).asObject());
        gdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getGoalDifference()).asObject());
        pointsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPoints()).asObject());
    }

    @FXML
    public void calculateStandings() {
        ObservableList<Team> teams = DataStore.getInstance().getTeams();
        ObservableList<Match> matches = DataStore.getInstance().getMatches();

        Map<Team, TeamStats> statsMap = new HashMap<>();

        // 1. Tüm takımlar için boş istatistik oluştur
        for (Team team : teams) {
            // "BAY" takımını lig tablosuna dahil etmeyelim
            if (!team.getName().equals("BAY")) {
                statsMap.put(team, new TeamStats(team));
            }
        }

        // 2. Oynanan maçları işle
        for (Match match : matches) {
            if (match.isPlayed()) {
                Team home = match.getHomeTeam();
                Team away = match.getAwayTeam();

                // Takımlar silinmişse veya BAY ise atla
                if (!statsMap.containsKey(home) || !statsMap.containsKey(away)) continue;

                TeamStats homeStats = statsMap.get(home);
                TeamStats awayStats = statsMap.get(away);

                int hScore = match.getHomeScore();
                int aScore = match.getAwayScore();

                // Oynanan maç sayısı
                homeStats.setPlayed(homeStats.getPlayed() + 1);
                awayStats.setPlayed(awayStats.getPlayed() + 1);

                // Goller
                homeStats.setGoalsFor(homeStats.getGoalsFor() + hScore);
                homeStats.setGoalsAgainst(homeStats.getGoalsAgainst() + aScore);

                awayStats.setGoalsFor(awayStats.getGoalsFor() + aScore);
                awayStats.setGoalsAgainst(awayStats.getGoalsAgainst() + hScore);

                // Sonuçlar
                if (hScore > aScore) {
                    homeStats.setWon(homeStats.getWon() + 1);
                    awayStats.setLost(awayStats.getLost() + 1);
                } else if (aScore > hScore) {
                    awayStats.setWon(awayStats.getWon() + 1);
                    homeStats.setLost(homeStats.getLost() + 1);
                } else {
                    homeStats.setDrawn(homeStats.getDrawn() + 1);
                    awayStats.setDrawn(awayStats.getDrawn() + 1);
                }
            }
        }

        // 3. Sıralama (Puan > Averaj > Atılan Gol)
        List<TeamStats> sortedStats = statsMap.values().stream()
                .sorted(Comparator.comparingInt(TeamStats::getPoints)
                        .thenComparingInt(TeamStats::getGoalDifference)
                        .thenComparingInt(TeamStats::getGoalsFor)
                        .reversed()) // Büyükten küçüğe
                .collect(Collectors.toList());

        leagueTable.setItems(FXCollections.observableArrayList(sortedStats));
    }
}
