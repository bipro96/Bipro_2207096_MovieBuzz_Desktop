package com.example.moviebuzz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignUpController {
    @FXML private TextField newEmail, newUsername;
    @FXML private PasswordField newPassword;
    private final SceneSwitcher sceneSwitcher = new SceneSwitcher();

    @FXML
    public void handleSignUp(ActionEvent event) {
        if (newUsername.getText().isEmpty() || newPassword.getText().isEmpty()) { return;}

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
        sceneSwitcher.switchScene(event, "Login.fxml");
    }
}