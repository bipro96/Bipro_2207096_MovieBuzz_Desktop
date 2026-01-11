package com.example.moviebuzz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
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

        String sql = "SELECT DISTINCT m.* FROM movies m " +
                "JOIN shows s ON m.title = s.movieTitle " +
                "WHERE s.status = 'Active'";

        try (Connection conn = DatabaseHandler.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Movie movie = new Movie(
                        rs.getString("title"), rs.getString("genre"),
                        rs.getString("duration"), rs.getString("posterPath")
                );
                movieFlowPane.getChildren().add(createMovieCard(movie));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createMovieCard(Movie movie) {
        VBox movieCard = new VBox(10);
        movieCard.setStyle("-fx-alignment: center; -fx-cursor: hand; -fx-padding: 10;");

        ImageView imageView = new ImageView();
        try {
            Image image = new Image(movie.getPosterPath(), 150, 225, true, true);
            imageView.setImage(image);
        } catch (Exception e) {
            System.err.println("Image Error: " + movie.getPosterPath());
        }

        Label titleLabel = new Label(movie.getTitle());
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
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
        return movieCard;
    }

    private void showMovieDetails(javafx.scene.input.MouseEvent event, Movie movie) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("movie-details.fxml"));
        Parent root = loader.load();

        MovieDetailsController controller = loader.getController();
        controller.setMovieDetails(movie);


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    @FXML
    private void handleRecharge() {
        String username = UserSession.getInstance().getUsername();
        TextInputDialog dialog = new TextInputDialog("500");
        dialog.setTitle("Account Recharge");
        dialog.setHeaderText("Add Funds for " + username);
        dialog.setContentText("Enter amount (BDT):");

        dialog.showAndWait().ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount > 0 && updateUserBalance(username, amount)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Added BDT " + amount);
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid amount.");
            }
        });
    }

    private boolean updateUserBalance(String username, double amount) {
        String sql = "UPDATE users SET balance = IFNULL(balance, 0) + ? WHERE username = ?";
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setString(2, username);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        UserSession.getInstance().setUsername(null);
        sceneSwitcher.switchScene(event, "Login.fxml");
    }
}