module com.example.moviebuzz {
    // Standard JavaFX Modules
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    // Database & API Modules
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires java.net.http; // <--- ADDED: Needed for API Search

    // JSON Parsing (Jackson)
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;

    // Firebase & Google Auth (If you are using them)
    requires firebase.admin;
    requires com.google.auth.oauth2;
    requires com.google.auth;

    // UI & Other Libraries
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    // Reflection permissions
    // We open to fxml for UI loading and databind for JSON parsing
    opens com.example.moviebuzz to javafx.fxml, com.fasterxml.jackson.databind;

    exports com.example.moviebuzz;
}