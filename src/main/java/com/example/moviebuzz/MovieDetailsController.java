package com.example.moviebuzz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button; // Added Import
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.IOException;

public class MovieDetailsController {
    @FXML private ImageView detailPoster;
    @FXML private Label detailGenre;
    @FXML private Label detailDuration;
    //@FXML private Label detailTitle;
    @FXML private Label titleMovie;
    private Movie currentMovie;
    private final SceneSwitcher sceneSwitcher = new SceneSwitcher();

    public void setMovieDetails(Movie movie) {
        this.currentMovie = movie;
        titleMovie.setText(movie.getTitle());
        detailGenre.setText("Genre: " +movie.getGenre());
        detailDuration.setText("Duration: " +movie.getDuration());

        if (movie.getPosterPath() != null && !movie.getPosterPath().isEmpty()) {
            try {
                detailPoster.setImage(new Image(movie.getPosterPath(), true));
            } catch (Exception e) { System.err.println("Error loading detail image"); }
        }
    }

    @FXML
    private void handleBookTicket(ActionEvent event) {
        System.out.println("Booking for: " + currentMovie.getTitle());
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        sceneSwitcher.switchScene(event, "customer-view.fxml");
    }
}