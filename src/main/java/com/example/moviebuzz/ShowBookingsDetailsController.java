package com.example.moviebuzz;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.sql.*;

public class ShowBookingsDetailsController {
    @FXML private Label showInfoLabel;
    @FXML private Label totalSoldLabel;
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> colUser;
    @FXML private TableColumn<Booking, Integer> colQty;
    @FXML private TableColumn<Booking, Double> colPaid;
    @FXML private TableColumn<Booking, String> colStatus;

    @FXML
    public void initialize() {

        colUser.setCellValueFactory(new PropertyValueFactory<>("username"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("ticketCount"));
        colPaid.setCellValueFactory(new PropertyValueFactory<>("amountPaid"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }


    public void loadBookingData(Show show) {
        showInfoLabel.setText("Movie: " + show.getMovieTitle() + " | " + show.getShowDate());

        ObservableList<Booking> data = FXCollections.observableArrayList();
        int activeTickets = 0;


        String sql = "SELECT username, ticketCount, amountPaid, status FROM bookings WHERE showId = ?";

        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, show.getShowId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String status = rs.getString("status");
                int qty = rs.getInt("ticketCount");

                data.add(new Booking(
                        rs.getString("username"),
                        qty,
                        rs.getDouble("amountPaid"),
                        status
                ));


                if ("Confirmed".equalsIgnoreCase(status)) {
                    activeTickets += qty;
                }
            }

            bookingsTable.setItems(data);
            totalSoldLabel.setText("Total Active Tickets Sold: " + activeTickets);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) bookingsTable.getScene().getWindow();
        stage.close();
    }
}