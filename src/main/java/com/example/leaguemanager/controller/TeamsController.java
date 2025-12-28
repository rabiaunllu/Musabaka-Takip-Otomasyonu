package com.example.leaguemanager.controller;

import com.example.leaguemanager.model.DataStore;
import com.example.leaguemanager.model.Team;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class TeamsController {

    @FXML
    private TableView<Team> teamsTable;
    @FXML
    private TableColumn<Team, String> colName;
    @FXML
    private TableColumn<Team, String> colCity;
    @FXML
    private TableColumn<Team, String> colStadium;

    @FXML
    private TextField txtName;
    @FXML
    private TextField txtCity;
    @FXML
    private TextField txtStadium;

    @FXML
    private Button btnAdd;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnGenerateFixtures;

    @FXML
    public void initialize() {
        // Initialize columns
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        colCity.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCity()));
        colStadium.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStadium()));

        // Bind table to DataStore
        teamsTable.setItems(DataStore.getInstance().getTeams());

        // Load dummy data if empty
        if (DataStore.getInstance().getTeams().isEmpty()) {
            DataStore.getInstance().loadDummyData();
        }

        // Add selection listener
        teamsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtName.setText(newSelection.getName());
                txtCity.setText(newSelection.getCity());
                txtStadium.setText(newSelection.getStadium());
            }
        });
    }

    @FXML
    private void handleAddTeam() {
        String name = txtName.getText().trim();
        String city = txtCity.getText().trim();
        String stadium = txtStadium.getText().trim();

        if (name.isEmpty() || city.isEmpty() || stadium.isEmpty()) {
            showAlert("Hata", "Lütfen tüm alanları doldurunuz.");
            return;
        }

        Team newTeam = new Team(name, city, stadium);
        DataStore.getInstance().getTeams().add(newTeam);
        DataStore.getInstance().saveData();
        clearFields();
    }

    @FXML
    private void handleDeleteTeam() {
        Team selectedTeam = teamsTable.getSelectionModel().getSelectedItem();
        if (selectedTeam != null) {
            DataStore.getInstance().getTeams().remove(selectedTeam);
            DataStore.getInstance().saveData();
            clearFields();
        } else {
            showAlert("Uyarı", "Lütfen silinecek bir takım seçiniz.");
        }
    }

    @FXML
    private void handleGenerateFixtures() {
        // Navigasyondan önce fikstürü oluştur
        boolean success = com.example.leaguemanager.model.DataStore.getInstance().generateFixedFixtures();
        
        if (success) {
            showAlert("Bilgi", "Fikstür oluşturuldu, yönlendiriliyorsunuz...");
        } else {
             showAlert("Uyarı", "Fikstür oluşturulamadı (Yetersiz takım). Yine de yönlendiriliyorsunuz.");
        }

        if (MainController.getInstance() != null) {
            MainController.getInstance().showFixtures();
        } else {
             System.out.println("MainController instance not found.");
             showAlert("Uyarı", "Navigasyon hatası.");
        }
    }

    private void clearFields() {
        txtName.clear();
        txtCity.clear();
        txtStadium.clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
