package com.example.moviebuzz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.sql.*;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorMessageLabel, loginHeader;
    @FXML private Hyperlink backButton, createAccountLink;
    @FXML private Button adminToggleButton;

    private boolean isAdminMode = false;

    private final SceneSwitcher sceneSwitcher = new SceneSwitcher();

    @FXML
    public void handleAdminToggle() {
        isAdminMode = !isAdminMode;
        loginHeader.setText(isAdminMode ? "Admin Portal" : "User Login");


        adminToggleButton.setVisible(!isAdminMode);
        backButton.setVisible(isAdminMode);
        createAccountLink.setVisible(!isAdminMode);

        usernameField.clear();
        passwordField.clear();
        errorMessageLabel.setVisible(false);
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        try (Connection conn = DatabaseHandler.connect()) {
            String sql = "SELECT role FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user);
            pstmt.setString(2, pass);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");


                if (isAdminMode && "admin".equalsIgnoreCase(role)) {
                    sceneSwitcher.switchScene(event, "admin-dashboard.fxml");
                } else if (!isAdminMode && "customer".equalsIgnoreCase(role)) {
                    sceneSwitcher.switchScene(event, "customer-view.fxml");
                } else {
                    showError("Access denied for this portal.");
                }
            } else {
                showError("Invalid credentials.");
            }
        } catch (SQLException | IOException e) {
            showError("Database Error: " + e.getMessage());
        }
    }

    @FXML
    public void goToSignUp(ActionEvent event) throws IOException {
        sceneSwitcher.switchScene(event, "sign-up.fxml");
    }

    private void showError(String msg) {
        errorMessageLabel.setText(msg);
        errorMessageLabel.setVisible(true);
    }
}