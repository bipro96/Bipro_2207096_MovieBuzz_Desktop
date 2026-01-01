package com.example.moviebuzz;

import javafx.beans.property.*;

public class Booking {
    private final StringProperty ticketCode;
    private final StringProperty movieTitle;
    private final StringProperty showDate;
    private final StringProperty showTime;
    private final DoubleProperty amountPaid;
    private final StringProperty status;


    private final StringProperty username;
    private final IntegerProperty ticketCount;


    public Booking(String ticketCode, String movieTitle, String showDate, String showTime, double amountPaid, String status) {
        this.ticketCode = new SimpleStringProperty(ticketCode);
        this.movieTitle = new SimpleStringProperty(movieTitle);
        this.showDate = new SimpleStringProperty(showDate);
        this.showTime = new SimpleStringProperty(showTime);
        this.amountPaid = new SimpleDoubleProperty(amountPaid);
        this.status = new SimpleStringProperty(status);
        this.username = new SimpleStringProperty("");
        this.ticketCount = new SimpleIntegerProperty(0);
    }


    public Booking(String username, int ticketCount, double amountPaid, String status) {
        this.username = new SimpleStringProperty(username);
        this.ticketCount = new SimpleIntegerProperty(ticketCount);
        this.amountPaid = new SimpleDoubleProperty(amountPaid);
        this.status = new SimpleStringProperty(status);

        this.ticketCode = new SimpleStringProperty("");
        this.movieTitle = new SimpleStringProperty("");
        this.showDate = new SimpleStringProperty("");
        this.showTime = new SimpleStringProperty("");
    }



    public String getUsername() { return username.get(); }
    public StringProperty usernameProperty() { return username; }

    public int getTicketCount() { return ticketCount.get(); }
    public IntegerProperty ticketCountProperty() { return ticketCount; }

    public String getTicketCode() { return ticketCode.get(); }
    public StringProperty ticketCodeProperty() { return ticketCode; }

    public String getMovieTitle() { return movieTitle.get(); }
    public StringProperty movieTitleProperty() { return movieTitle; }

    public String getShowDate() { return showDate.get(); }
    public StringProperty showDateProperty() { return showDate; }

    public String getShowTime() { return showTime.get(); }
    public StringProperty showTimeProperty() { return showTime; }

    public double getAmountPaid() { return amountPaid.get(); }
    public DoubleProperty amountPaidProperty() { return amountPaid; }

    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }
}