package com.example.moviebuzz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;

public class SignUpController {
    @FXML private TextField newEmail, newUsername;
    @FXML private PasswordField newPassword;

    @FXML
    public void handleSignUp(ActionEvent event) {
        if (newUsername.getText().isEmpty() || newPassword.getText().isEmpty()) {
            return;
        }

        String sql = "INSERT INTO users(username, password, role) VALUES(?, ?, 'customer')";
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newUsername.getText());
            pstmt.setString(2, newPassword.getText());
            pstmt.executeUpdate();

            backToLogin(event);
        } catch (SQLException | IOException e) {
            System.err.println("Database Error: " + e.getMessage());
        }
    }

    @FXML
    public void backToLogin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load(), 900, 600));
    }
}