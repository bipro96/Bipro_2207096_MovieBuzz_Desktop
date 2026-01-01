package com.example.moviebuzz;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;

public class ManageShowsController {
    @FXML private TableView<Show> showsTable;
    @FXML private TableColumn<Show, String> colMovie, colDate, colTime, colStatus;
    @FXML private TableColumn<Show, Double> colPrice;

    private final SceneSwitcher sceneSwitcher = new SceneSwitcher();

    @FXML
    public void initialize() {
        colMovie.setCellValueFactory(new PropertyValueFactory<>("movieTitle"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("showDate"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("showTime"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        loadShows();
    }

    private void loadShows() {
        ObservableList<Show> showList = FXCollections.observableArrayList();
        try (Connection conn = DatabaseHandler.connect();
             ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM shows")) {
            while (rs.next()) {
                showList.add(new Show(rs.getInt("showId"), rs.getString("movieTitle"),
                        rs.getString("showDate"), rs.getString("showTime"),
                        rs.getDouble("price"), rs.getString("status")));
            }
            showsTable.setItems(showList);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleViewBookings(ActionEvent event) {
        Show selected = showsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Required", "Please select a show from the table first.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("show-bookings-details.fxml"));
            Parent root = loader.load();


            ShowBookingsDetailsController controller = loader.getController();
            controller.loadBookingData(selected);

            Stage stage = new Stage();
            stage.setTitle("Customer List - " + selected.getMovieTitle());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Blocks interaction with main window
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelAndRefund() {
        Show selected = showsTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getStatus().equalsIgnoreCase("Cancelled")) return;

        try (Connection conn = DatabaseHandler.connect()) {
            conn.setAutoCommit(false);

            String cancelShow = "UPDATE shows SET status = 'Cancelled' WHERE showId = ?";
            try (PreparedStatement ps = conn.prepareStatement(cancelShow)) {
                ps.setInt(1, selected.getShowId());
                ps.executeUpdate();
            }

            String fetchRefunds = "SELECT username, amountPaid FROM bookings WHERE showId = ? AND status = 'Confirmed'";
            try (PreparedStatement psFetch = conn.prepareStatement(fetchRefunds)) {
                psFetch.setInt(1, selected.getShowId());
                ResultSet rs = psFetch.executeQuery();

                while (rs.next()) {
                    String userToRefund = rs.getString("username");
                    double amountToRefund = rs.getDouble("amountPaid");

                    String updateBalance = "UPDATE users SET balance = balance + ? WHERE username = ?";
                    try (PreparedStatement psBal = conn.prepareStatement(updateBalance)) {
                        psBal.setDouble(1, amountToRefund);
                        psBal.setString(2, userToRefund);
                        psBal.executeUpdate();
                    }
                }
            }

            String updateBookings = "UPDATE bookings SET status = 'Refunded' WHERE showId = ? AND status = 'Confirmed'";
            try (PreparedStatement psBook = conn.prepareStatement(updateBookings)) {
                psBook.setInt(1, selected.getShowId());
                psBook.executeUpdate();
            }

            conn.commit();
            loadShows();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML private void handleBack(ActionEvent event) throws IOException {
        sceneSwitcher.switchScene(event, "admin-dashboard.fxml");
    }
}