package com.example.leaguemanager;

import com.example.leaguemanager.model.DataStore;
import com.example.leaguemanager.model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        User savedUser = DataStore.getInstance().loadSession();
        
        String fxmlFile = (savedUser != null) ? "MainLayout.fxml" : "Login.fxml";
        String title = (savedUser != null) 
            ? "Lig Yöneticisi - " + savedUser.getUsername() 
            : "Lig Yöneticisi - Giriş";

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlFile));
        Scene scene = new Scene(fxmlLoader.load(), 1100, 800);
        
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
        
        if (savedUser != null) {
            DataStore.getInstance().setCurrentUser(savedUser);
        }
    }
}
