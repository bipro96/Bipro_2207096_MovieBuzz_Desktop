package com.example.moviebuzz;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
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

        HttpClient.newHttpClient().sendAsync(HttpRequest.newBuilder().uri(URI.create(url)).build(), HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(this::parseAndSaveMovie);
    }

    private void parseAndSaveMovie(String json) {
        try {
            Movie movie = new ObjectMapper().readValue(json, Movie.class);
            if (movie.getTitle() != null) {
                saveToDatabase(movie);
                Platform.runLater(() -> {
                    statusLabel.setText("Saved: " + movie.getTitle());
                    loadMoviesFromDatabase();
                });
            } else {
                Platform.runLater(() -> statusLabel.setText("Movie not found."));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleScheduleShow(ActionEvent event) throws IOException {
        Movie selected = movieTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select a movie first!");
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("schedule-movie.fxml"));
        Parent root = loader.load();

        ScheduleMovieController controller = loader.getController();
        controller.setMovieTitle(selected.getTitle());


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    @FXML
    private void handleViewShows(ActionEvent event) throws IOException {
        sceneSwitcher.switchScene(event, "manage-shows.fxml");
    }

    @FXML
    private void handleDeleteMovie() {
        Movie selected = movieTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        String sql = "DELETE FROM movies WHERE title = ?";
        try (Connection conn = DatabaseHandler.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, selected.getTitle());
            pstmt.executeUpdate();
            loadMoviesFromDatabase();
            statusLabel.setText("Deleted: " + selected.getTitle());
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        sceneSwitcher.switchScene(event, "Login.fxml");
    }

    private void saveToDatabase(Movie movie) {
        String sql = "INSERT OR IGNORE INTO movies(title, genre, duration, posterPath) VALUES(?,?,?,?)";
        try (Connection conn = DatabaseHandler.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, movie.getTitle());
            pstmt.setString(2, movie.getGenre());
            pstmt.setString(3, movie.getDuration());
            pstmt.setString(4, movie.getPosterPath());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadMoviesFromDatabase() {
        ObservableList<Movie> movieList = FXCollections.observableArrayList();
        try (Connection conn = DatabaseHandler.connect();
             ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM movies")) {
            while (rs.next()) {
                movieList.add(new Movie(rs.getString("title"), rs.getString("genre"), rs.getString("duration"), rs.getString("posterPath")));
            }
            movieTable.setItems(movieList);
        } catch (SQLException e) { e.printStackTrace(); }
    }
}