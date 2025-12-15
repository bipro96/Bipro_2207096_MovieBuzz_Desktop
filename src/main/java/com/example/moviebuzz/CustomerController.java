package com.example.moviebuzz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.sql.*;

public class CustomerController {
    @FXML private FlowPane movieFlowPane;

    private final SceneSwitcher sceneSwitcher = new SceneSwitcher();

    @FXML
    public void initialize() {
        loadMovies();
    }

    private void loadMovies() {
        movieFlowPane.getChildren().clear();
        String sql = "SELECT * FROM movies";

        try (Connection conn = DatabaseHandler.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String title = rs.getString("title");
                String posterUrl = rs.getString("posterPath");


                VBox movieCard = new VBox(10);
                movieCard.setStyle("-fx-alignment: center;");

                ImageView imageView = new ImageView();
                try {
                    Image image = new Image(posterUrl, 150, 225, true, true);
                    imageView.setImage(image);
                } catch (Exception e) {
                    System.err.println("Could not load image: " + posterUrl);
                }

                Label titleLabel = new Label(title);
                titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                titleLabel.setWrapText(true);
                titleLabel.setMaxWidth(150);

                movieCard.getChildren().addAll(imageView, titleLabel);
                movieFlowPane.getChildren().add(movieCard);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        sceneSwitcher.switchScene(event, "Login.fxml");
    }
}