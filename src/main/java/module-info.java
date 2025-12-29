module com.example.leaguemanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens com.example.leaguemanager to javafx.fxml;
    opens com.example.leaguemanager.controller to javafx.fxml;
    exports com.example.leaguemanager;
    exports com.example.leaguemanager.controller;
}