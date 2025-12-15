package com.example.moviebuzz;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Movie {
    @JsonProperty("Title")
    private String title;

    @JsonProperty("Genre")
    private String genre;

    @JsonProperty("Runtime")
    private String duration;

    @JsonProperty("Poster")
    private String posterPath;

    public Movie() {}

    public Movie(String title, String genre, String duration, String posterPath) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.posterPath = posterPath;
    }


    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public String getDuration() { return duration; }
    public String getPosterPath() { return posterPath; }
}