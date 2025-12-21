package com.example.moviebuzz;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class SceneSwitcher {
    public void switchScene(ActionEvent event, String fxmlFile) throws IOException {

        URL fxml = getClass().getResource(fxmlFile);
        if (fxml == null) {
            throw new IOException("Cannot find FXML file: " + fxmlFile); }

        FXMLLoader loader = new FXMLLoader(fxml);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load(), 900, 600));
    }
}