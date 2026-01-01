package com.example.moviebuzz;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.sql.*;

public class MovieDetailsController {
    @FXML private ImageView detailPoster;
    @FXML private Label detailTitle, detailGenre, balanceLabel, statusLabel;
    @FXML private VBox historyUI;
    @FXML private Label lblBookingID, lblDateTime, lblTickets, lblTotalPaid;
    @FXML private TableView<Show> showTable;
    @FXML private TableColumn<Show, String> colDate, colTime;
    @FXML private TableColumn<Show, Double> colPrice;
    @FXML private Spinner<Integer> ticketSpinner;

    private Movie currentMovie;
    private final SceneSwitcher sceneSwitcher = new SceneSwitcher();

    @FXML
    public void initialize() {
        colDate.setCellValueFactory(new PropertyValueFactory<>("showDate"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("showTime"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        ticketSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        displayBalance();
    }

    public void setMovieDetails(Movie movie) {
        this.currentMovie = movie;
        detailTitle.setText(movie.getTitle());
        detailGenre.setText(movie.getGenre() + " | " + movie.getDuration());
        try {
            detailPoster.setImage(new Image(movie.getPosterPath(), true));
        } catch (Exception e) { e.printStackTrace(); }

        loadMovieStatus();
    }

    private void displayBalance() {
        String user = UserSession.getInstance().getUsername();
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement("SELECT balance FROM users WHERE username = ?")) {
            ps.setString(1, user);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                balanceLabel.setText("Balance: BDT " + String.format("%.2f", rs.getDouble("balance")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadMovieStatus() {
        String user = UserSession.getInstance().getUsername();
        if (currentMovie == null) return;

        String sql = "SELECT SUM(ticketCount) as totalTickets, SUM(amountPaid) as totalPaid, " +
                "GROUP_CONCAT(bookingId) as ids, MAX(showDate) as lastDate, MAX(showTime) as lastTime " +
                "FROM bookings WHERE username = ? AND movieTitle = ? AND status = 'Confirmed'";

        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user);
            ps.setString(2, currentMovie.getTitle());
            ResultSet rs = ps.executeQuery();

            if (rs.next() && rs.getInt("totalTickets") > 0) {
                historyUI.setVisible(true);
                historyUI.setManaged(true);
                lblTickets.setText("Purchased: " + rs.getInt("totalTickets") + " ticket(s)");
                lblBookingID.setText("ID: #" + rs.getString("ids"));
                lblDateTime.setText("Show: " + rs.getString("lastDate") + " @ " + rs.getString("lastTime"));
                lblTotalPaid.setText("Total: BDT " + String.format("%.2f", rs.getDouble("totalPaid")));
            } else {
                historyUI.setVisible(false);
                historyUI.setManaged(false);
            }
            loadAvailableShows();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadAvailableShows() {
        ObservableList<Show> list = FXCollections.observableArrayList();
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM shows WHERE movieTitle = ? AND status = 'Active'")) {
            ps.setString(1, currentMovie.getTitle());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Show(rs.getInt("showId"), rs.getString("movieTitle"),
                        rs.getString("showDate"), rs.getString("showTime"),
                        rs.getDouble("price"), rs.getString("status")));
            }
            showTable.setItems(list);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleConfirmBooking(ActionEvent event) {
        Show selected = showTable.getSelectionModel().getSelectedItem();
        String user = UserSession.getInstance().getUsername();
        int count = ticketSpinner.getValue();

        if (selected == null) {
            statusLabel.setText("Please select a showtime!");
            return;
        }

        double totalCost = selected.getPrice() * count;

        try (Connection conn = DatabaseHandler.connect()) {
            conn.setAutoCommit(false);
            PreparedStatement psBal = conn.prepareStatement(
                    "UPDATE users SET balance = balance - ? WHERE username = ? AND balance >= ?");
            psBal.setDouble(1, totalCost);
            psBal.setString(2, user);
            psBal.setDouble(3, totalCost);

            if (psBal.executeUpdate() > 0) {
                String sql = "INSERT INTO bookings(username, showId, movieTitle, showDate, showTime, amountPaid, ticketCount, status) VALUES(?,?,?,?,?,?,?,'Confirmed')";
                try (PreparedStatement psIns = conn.prepareStatement(sql)) {
                    psIns.setString(1, user);
                    psIns.setInt(2, selected.getShowId());
                    psIns.setString(3, currentMovie.getTitle());
                    psIns.setString(4, selected.getShowDate());
                    psIns.setString(5, selected.getShowTime());
                    psIns.setDouble(6, totalCost);
                    psIns.setInt(7, count);
                    psIns.executeUpdate();
                }
                conn.commit();
                displayBalance();
                loadMovieStatus();
            } else {
                statusLabel.setText("Insufficient balance!");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML private void handleBack(ActionEvent event) throws IOException {
        sceneSwitcher.switchScene(event, "customer-view.fxml");
    }
}