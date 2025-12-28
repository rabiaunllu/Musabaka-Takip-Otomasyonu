package com.example.leaguemanager.controller;

import com.example.leaguemanager.model.DataStore;
import com.example.leaguemanager.model.Match;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class LiveMatchController {

    @FXML private Label homeTeamLabel;
    @FXML private Label awayTeamLabel;
    @FXML private Label homeScoreLabel;
    @FXML private Label awayScoreLabel;
    @FXML private Label lblSure; // FXML ID: lblSure (Time)
    @FXML private Label matchTimeLabel; // Top header time label if exists, else we use Pulse Label or update title
    @FXML private Circle livePulseCircle;
    @FXML private VBox eventsContainer;
    @FXML private VBox adminControlsArea;
    
    @FXML private Button btnBaslat;
    @FXML private Button btnDurdur;

    private Match currentMatch;
    private int matchSeconds = 0;
    private int homeScore = 0;
    private int awayScore = 0;
    private Timeline timeline;
    private boolean isTimerRunning = false;
    private static final String INFO_COLOR = "#cccccc";

    @FXML
    public void initialize() {
        startPulseAnimation();
        loadNextMatch();
        
        // Role-based Visibility
        if (DataStore.getInstance().getCurrentUser() != null &&
            DataStore.getInstance().getCurrentUser().getRole() == com.example.leaguemanager.model.User.Role.USER) {
            adminControlsArea.setVisible(false);
        }

        // Role-based Timer Speed
        Duration tickDuration = Duration.seconds(1); // Default: 90 mins = 90 real seconds
        if (DataStore.getInstance().getCurrentUser() != null &&
            DataStore.getInstance().getCurrentUser().getRole() == com.example.leaguemanager.model.User.Role.DEVELOPER) {
            // Developer speed: 90 mins in 60s -> 666ms per game-minute. 
            // Our logic ticks every game-second, so 666ms / 60 = 11.1ms.
            tickDuration = Duration.millis(11); 
        }

        timeline = new Timeline(new KeyFrame(tickDuration, e -> {
            matchSeconds++;
            updateTimeLabel();
            
            // Advanced Rules (Pauses at 45, 90, 105. Finish at 120)
            if (matchSeconds == 2700) { // 45:00
                handleStopTimer();
                logEvent("Devre Arası (45:00)", INFO_COLOR);
            } else if (matchSeconds == 5400) { // 90:00
                handleStopTimer();
                logEvent("Normal Süre Bitti (90:00)", INFO_COLOR);
            } else if (matchSeconds == 6300) { // 105:00
                handleStopTimer();
                logEvent("Uzatma Devre Arası (105:00)", INFO_COLOR);
            } else if (matchSeconds == 7200) { // 120:00
                handleEndMatch(); // Auto-finish
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    private void loadNextMatch() {
        // Find first unplayed match
        currentMatch = DataStore.getInstance().getMatches().stream()
                .filter(m -> !m.isPlayed())
                .findFirst()
                .orElse(null);

        if (currentMatch == null) {
            showAlert("Sezon Tamamlandı", "Tüm maçlar oynandı!");
            homeTeamLabel.setText("-");
            awayTeamLabel.setText("-");
            return;
        }

        homeTeamLabel.setText(currentMatch.getHomeTeam().getName());
        awayTeamLabel.setText(currentMatch.getAwayTeam().getName());
        homeScore = 0;
        awayScore = 0;
        matchSeconds = 0;
        updateScoreLabels();
        updateTimeLabel();
        eventsContainer.getChildren().clear();
    }

    private void startPulseAnimation() {
        if (livePulseCircle != null) {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.8), livePulseCircle);
            scaleTransition.setFromX(1.0);
            scaleTransition.setFromY(1.0);
            scaleTransition.setToX(1.5);
            scaleTransition.setToY(1.5);
            scaleTransition.setAutoReverse(true);
            scaleTransition.setCycleCount(Animation.INDEFINITE);
            scaleTransition.play();
        }
    }

    private void updateTimeLabel() {
        int mins = matchSeconds / 60;
        int secs = matchSeconds % 60;
        String timeStr = String.format("%02d:%02d", mins, secs);

        if (lblSure != null) {
            lblSure.setText(timeStr);
        }
        
        if (matchTimeLabel != null) {
            matchTimeLabel.setText("CANLI - " + timeStr);
        }
    }

    private void updateScoreLabels() {
        homeScoreLabel.setText(String.valueOf(homeScore));
        awayScoreLabel.setText(String.valueOf(awayScore));
    }

    @FXML
    public void handleStartTimer() {
        // Allow start if timer not running. 
        // 90+ can continue (overtime simulation logic or just keep running)
        if (!isTimerRunning && currentMatch != null) {
            timeline.play();
            isTimerRunning = true;
            btnBaslat.setDisable(true);
            btnDurdur.setDisable(false);
            
            if (matchSeconds == 0) {
                logEvent("Maç Başladı!", INFO_COLOR);
            } else {
                logEvent("Maç Devam Ediyor...", INFO_COLOR);
            }
        }
    }

    @FXML
    public void handleStopTimer() {
        if (isTimerRunning) {
            timeline.pause();
            isTimerRunning = false;
            btnBaslat.setDisable(false);
            btnDurdur.setDisable(true);
            logEvent("Maç Duraklatıldı.", INFO_COLOR);
        }
    }

    @FXML
    public void handleAddHomeGoal() {
        if (currentMatch == null) return;
        homeScore++;
        updateScoreLabels();
        logEvent("GOL! " + currentMatch.getHomeTeam().getName(), "#4ade80");
        flashScore(homeScoreLabel);
    }

    @FXML
    public void handleAddAwayGoal() {
        if (currentMatch == null) return;
        awayScore++;
        updateScoreLabels();
        logEvent("GOL! " + currentMatch.getAwayTeam().getName(), "#e879f9");
        flashScore(awayScoreLabel);
    }

    @FXML
    public void handleAddHomeCard() {
        if (currentMatch == null) return;
        logEvent("Sarı Kart - " + currentMatch.getHomeTeam().getName(), "#facc15");
    }

    @FXML
    public void handleAddAwayCard() {
        if (currentMatch == null) return;
        logEvent("Sarı Kart - " + currentMatch.getAwayTeam().getName(), "#facc15");
    }
    
    @FXML
    public void handleAddHomeRedCard() {
        if (currentMatch == null) return;
        logEvent("KIRMIZI KART - " + currentMatch.getHomeTeam().getName(), "#ef4444");
    }

    @FXML
    public void handleAddAwayRedCard() {
        if (currentMatch == null) return;
        logEvent("KIRMIZI KART - " + currentMatch.getAwayTeam().getName(), "#ef4444");
    }

    @FXML
    public void handleEndMatch() {
        if (currentMatch == null) return;

        // Stop timer
        timeline.stop();
        isTimerRunning = false;
        btnBaslat.setDisable(false);
        
        // Save result
        currentMatch.setHomeScore(homeScore);
        currentMatch.setAwayScore(awayScore);
        currentMatch.setPlayed(true);
        DataStore.getInstance().saveData();

        showAlert("Maç Bitti", "Sonuç: " + homeScore + " - " + awayScore + "\nVeritabanına kaydedildi.");
        
        // Load next
        loadNextMatch();
    }

    private void logEvent(String text, String colorHexOrStyle) {
        // Create custom Log Row similar to FXML
        // HBox -> Label(Min) -> VBox(Title, Desc)
        
        HBox row = new HBox(10);
        row.setStyle("-fx-background-color: rgba(30, 41, 59, 0.8); -fx-border-color: rgba(51, 65, 85, 1); -fx-background-radius: 5; -fx-border-radius: 5; -fx-padding: 10;");
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        int mins = matchSeconds / 60; // Calculate minutes from seconds
        Label timeLbl = new Label(mins + "'");
        timeLbl.setMinWidth(30);
        timeLbl.setStyle("-fx-text-fill: #22d3ee; -fx-font-weight: bold;");
        
        Label eventLbl = new Label(text);
        if (colorHexOrStyle != null && colorHexOrStyle.startsWith("#")) {
             eventLbl.setStyle("-fx-text-fill: " + colorHexOrStyle + "; -fx-font-size: 13px;");
        } else {
             // Fallback or specific style class
             eventLbl.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        }

        row.getChildren().addAll(timeLbl, eventLbl);
        
        // Add to top
        eventsContainer.getChildren().add(0, row);
    }

    private void flashScore(Label label) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), label);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.5);
        st.setToY(1.5);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
