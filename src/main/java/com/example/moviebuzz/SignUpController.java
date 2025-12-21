package com.example.moviebuzz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.sql.*;

public class SignUpController {
    @FXML private TextField newUsername;
    @FXML private PasswordField newPassword;
    @FXML private Label errorMessageLabel;

    private final SceneSwitcher sceneSwitcher = new SceneSwitcher();

    @FXML
    public void handleSignUp(ActionEvent event) {
        String user = newUsername.getText().trim();
        String pass = newPassword.getText().trim();
        if (user.isEmpty()||pass.isEmpty()) {
            showError("All fields are required!");
            return;
        }
        try (Connection conn = DatabaseHandler.connect()) {
            String sql = "INSERT INTO users(username, password, role) VALUES(?, ?, 'customer')";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, user);
                pstmt.setString(2, pass);
                pstmt.executeUpdate();
                sceneSwitcher.switchScene(event, "Login.fxml");
            } catch (SQLException e) {

                if (e.getMessage().contains("UNIQUE constraint failed: users.username")) {
                    showError("Username is already taken.");
                } else {
                    showError("Database Error: " + e.getMessage());
                }
            }

        } catch (SQLException | IOException e) {
            showError("System Error: " + e.getMessage());
        }
    }
    @FXML
    public void backToLogin(ActionEvent event) throws IOException {
        sceneSwitcher.switchScene(event, "Login.fxml");
    }
    private void showError(String msg) {
        errorMessageLabel.setText(msg);
        errorMessageLabel.setVisible(true);
    }
}