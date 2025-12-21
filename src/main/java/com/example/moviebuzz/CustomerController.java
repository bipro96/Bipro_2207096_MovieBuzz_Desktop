package com.example.moviebuzz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;

public class CustomerController {
    @FXML private FlowPane movieFlowPane;
    private final SceneSwitcher sceneSwitcher = new SceneSwitcher();
    @FXML
    public void initialize() {loadMovies();}
    private void loadMovies() {
        movieFlowPane.getChildren().clear();
        String sql = "SELECT * FROM movies";

        try (Connection conn = DatabaseHandler.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Movie movie = new Movie(
                        rs.getString("title"), rs.getString("genre"), rs.getString("duration"), rs.getString("posterPath"));
                VBox movieCard = new VBox(10);
                movieCard.getStyleClass().add("movie-card");
                movieCard.setStyle("-fx-alignment: center; -fx-cursor: hand; -fx-padding: 10;");

                ImageView imageView = new ImageView();
                try {
                    Image image = new Image(movie.getPosterPath(), 150, 225, true, true);
                    imageView.setImage(image);
                } catch (Exception e) {System.err.println("Image load error: " + movie.getPosterPath());}

                Label titleLabel = new Label(movie.getTitle());
                titleLabel.setWrapText(true);
                titleLabel.setMaxWidth(150);
                movieCard.getChildren().addAll(imageView, titleLabel);
                movieCard.setOnMouseClicked(event -> {
                    try {
                        showMovieDetails(event, movie);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                movieFlowPane.getChildren().add(movieCard);
            }
        } catch (SQLException e) {e.printStackTrace();}
    }

    private void showMovieDetails(javafx.scene.input.MouseEvent event, Movie movie) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("movie-details.fxml"));
        Parent root = loader.load();
        MovieDetailsController controller = loader.getController();
        controller.setMovieDetails(movie);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 900, 600));
        stage.show();
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        sceneSwitcher.switchScene(event, "Login.fxml"); // Return to login
    }
}