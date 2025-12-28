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
    @FXML private TableColumn<Match, Void> actionsColumn;
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
        ObservableList<Team> teams = DataStore.getInstance().getTeams();
        
        if (teams.isEmpty() || teams.size() < 2) {
            showAlert("Yetersiz Takım", "Fikstür oluşturmak için en az 2 takım gereklidir.");
            return;
        }

        // Eğer maçlar zaten varsa uyar, üzerine yaz veya temizle
        if (!DataStore.getInstance().getMatches().isEmpty()) {
             // Basitlik için direkt temizliyoruz, gerçek uygulamada onay istenebilir
             DataStore.getInstance().getMatches().clear();
        }

        List<Team> teamList = new ArrayList<>(teams);
        
        // Takım sayısı tek ise BAY (Dummy) takımı ekle
        if (teamList.size() % 2 != 0) {
            teamList.add(new Team("BAY", "-", "-"));
        }

        int numTeams = teamList.size();
        int numRounds = (numTeams - 1) * 2; // Çift devre (League format)
        int matchesPerRound = numTeams / 2;

        List<Match> allMatches = new ArrayList<>();
        
        // Round Robin Algoritması
        // İlk yarı
        for (int round = 0; round < numTeams - 1; round++) {
            for (int matchIdx = 0; matchIdx < matchesPerRound; matchIdx++) {
                int homeIdx = (round + matchIdx) % (numTeams - 1);
                int awayIdx = (numTeams - 1 - matchIdx + round) % (numTeams - 1);

                // Son takım (sabit) için özel durum - döngüsel kaydırma dışı
                if (matchIdx == 0) {
                    awayIdx = numTeams - 1;
                }

                Team home = teamList.get(homeIdx);
                Team away = teamList.get(awayIdx);

                // "BAY" takımı içeren maçları atla veya ekle (genelde listelenmez ama hafta boş geçer)
                // Burada "BAY" olan maçları da oluşturuyoruz ama UI'da filtreleyebiliriz ya da "BAY" diye gösterebiliriz.
                // Kullanıcı isteğine göre "BAY" takımını gizleyebiliriz ama algoritma için gerekli.
                // Eğer takımlardan biri "BAY" ise, diğer takım o hafta maç yapmaz.
                
                // Ev sahibi/Deplasman dengesi için round tek/çift kontrolü (daha iyi dağılım için)
                if (matchIdx == 0 && round % 2 == 1) {
                    // Swap to balance home/away for the fixed team
                    Team temp = home;
                    home = away;
                    away = temp;
                }

                // Hafta numarası: round + 1
                int currentWeek = round + 1;
                
                // Maç tarihi (Basitçe her hafta 1 hafta sonrasına)
                // Başlangıç: Bugün + (Hafta-1)*7 gün
                LocalDateTime matchDate = LocalDateTime.now().plusDays((currentWeek - 1) * 7);

                // BAY kontrolü (İsteğe bağlı: isPlayed true/false veya özel statü)
                // Şimdilik normal maç gibi ekliyoruz, ismi "BAY" olacak.
                
                allMatches.add(new Match(home, away, matchDate, currentWeek));
            }
        }

        // İkinci yarı (Rövanş)
        for (int round = 0; round < numTeams - 1; round++) {
            for (int matchIdx = 0; matchIdx < matchesPerRound; matchIdx++) {
                // İlk yarıdaki maçları bulup tersini alacağız aslında ama
                // Algoritmayı tekrar çalıştırmak yerine ilk yarı mantığını ters çevirebiliriz.
                // Veya yukarıdaki döngüyü tekrar edip home/away swap yapabiliriz.
                
                // Basit yol: Yukarıdaki mantığı kopyala ama home/away yer değiştir ve hafta + (numTeams-1)
                
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

                // Rövanş: Ev sahibi ve deplasman yer değiştirir
                int secondHalfWeek = (round + 1) + (numTeams - 1);
                LocalDateTime matchDate = LocalDateTime.now().plusDays((secondHalfWeek - 1) * 7);
                
                allMatches.add(new Match(away, home, matchDate, secondHalfWeek));
            }
        }

        // Maçları DataStore'a ve UI'a ekle
        DataStore.getInstance().getMatches().addAll(allMatches);
        
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
