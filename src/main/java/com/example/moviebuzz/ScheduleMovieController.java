package com.example.moviebuzz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ScheduleMovieController {
    @FXML private TextField titleField;
    @FXML private TextField priceField;


    @FXML private TextField timeField;

    @FXML private DatePicker datePicker;

    private final SceneSwitcher sceneSwitcher = new SceneSwitcher();


    @FXML
    public void initialize() {

    }

    public void setMovieTitle(String title) {
        titleField.setText(title);
    }

    @FXML
    private void handleSaveShow(ActionEvent event) {
        String title = titleField.getText();
        String date = (datePicker.getValue() != null) ? datePicker.getValue().toString() : "";
        String time = timeField.getText(); // Reading from the manual text field
        String price = priceField.getText();

        if (date.isEmpty() || time.isEmpty() || price.isEmpty()) {
            System.err.println("Error: Please fill in all fields.");
            return;
        }

        String sql = "INSERT INTO shows(movieTitle, showDate, showTime, price, status) VALUES(?,?,?,?, 'Active')";

        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.setString(2, date);
            pstmt.setString(3, time);
            pstmt.setDouble(4, Double.parseDouble(price));
            pstmt.executeUpdate();

            sceneSwitcher.switchScene(event, "admin-dashboard.fxml");

        } catch (SQLException | IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) throws IOException {
        sceneSwitcher.switchScene(event, "admin-dashboard.fxml");
    }
}