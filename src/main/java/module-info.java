module com.example.leaguemanager {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.leaguemanager to javafx.fxml;
    exports com.example.leaguemanager;
}