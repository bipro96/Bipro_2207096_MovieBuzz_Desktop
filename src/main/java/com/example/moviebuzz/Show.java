package com.example.moviebuzz;

public class Show {
    private int showId;
    private String movieTitle, showDate, showTime, status;
    private double price;

    public Show(int showId, String movieTitle, String showDate, String showTime, double price, String status) {
        this.showId = showId;
        this.movieTitle = movieTitle;
        this.showDate = showDate;
        this.showTime = showTime;
        this.price = price;
        this.status = status;
    }


    public int getShowId() { return showId; }
    public String getMovieTitle() { return movieTitle; }
    public String getShowDate() { return showDate; }
    public String getShowTime() { return showTime; }
    public double getPrice() { return price; }
    public String getStatus() { return status; }
}