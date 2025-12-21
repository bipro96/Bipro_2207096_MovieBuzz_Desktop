package com.example.moviebuzz;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;

public class AdminController {
    @FXML private TextField apiSearchField;
    @FXML private Label statusLabel;
    @FXML private TableView<Movie> movieTable;
    @FXML private TableColumn<Movie, String> colTitle, colGenre, colDuration, colPoster;

    private final String API_KEY = "48e4feec";
    private final SceneSwitcher sceneSwitcher = new SceneSwitcher();

    @FXML
    public void initialize() {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        colPoster.setCellValueFactory(new PropertyValueFactory<>("posterPath"));

        loadMoviesFromDatabase();
    }

    @FXML
    private void handleApiSearch() {
        String query = apiSearchField.getText().trim().replace(" ", "+");
        if (query.isEmpty()) return;
        statusLabel.setText("Searching OMDb...");
        String url = "http://www.omdbapi.com/?t=" + query + "&apikey=" + API_KEY;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(this::parseAndSaveMovie)
                .exceptionally(ex -> {
                    Platform.runLater(() -> statusLabel.setText("Network Error: " + ex.getMessage()));
                    return null;
                });
    }

    private void parseAndSaveMovie(String json) {
        System.out.println("RAW JSON: " + json);
        try {
            ObjectMapper mapper = new ObjectMapper();
            Movie movie = mapper.readValue(json, Movie.class);
            if (movie.getTitle() == null) {
                Platform.runLater(() -> statusLabel.setText("Movie not found in API."));
                return;}
            saveToDatabase(movie);
            Platform.runLater(() -> {
                statusLabel.setText("Saved: " + movie.getTitle());
                apiSearchField.clear();
                loadMoviesFromDatabase();
            });
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void saveToDatabase(Movie movie) {
        String sql = "INSERT OR IGNORE INTO movies(title, genre, duration, posterPath) VALUES(?,?,?,?)";
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, movie.getTitle());
            pstmt.setString(2, movie.getGenre());
            pstmt.setString(3, movie.getDuration());
            pstmt.setString(4, movie.getPosterPath());
            pstmt.executeUpdate();
            System.out.println("Inserted into DB: " + movie.getTitle());
        } catch (SQLException e) {
            System.err.println("Database Save Error: "+e.getMessage());
        }
    }

    private void loadMoviesFromDatabase() {
        ObservableList<Movie> movieList = FXCollections.observableArrayList();
        String sql = "SELECT*FROM movies";

        try (Connection conn = DatabaseHandler.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                movieList.add(new Movie(
                        rs.getString("title"), rs.getString("genre"),
                        rs.getString("duration"), rs.getString("posterPath")
                ));}
            movieTable.setItems(movieList);
        } catch (SQLException e) { System.err.println("Load Table Error: " + e.getMessage());
        }
    }
    @FXML private void handleDeleteMovie() {
        Movie selected = movieTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        String sql = "DELETE FROM movies WHERE title = ?";
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, selected.getTitle());
            pstmt.executeUpdate(); loadMoviesFromDatabase();
            statusLabel.setText("Deleted movie: " + selected.getTitle());
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML private void handleLogout(ActionEvent event) throws IOException {
        sceneSwitcher.switchScene(event, "Login.fxml");
    }
}