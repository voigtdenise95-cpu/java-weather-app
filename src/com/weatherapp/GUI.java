package com.weatherapp;

import com.weatherapp.utils.HistoryManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/*
 * This class handles everything the user interacts with.
 * It builds the interface, captures input, and displays results.
 * It also connects the UI to the WeatherService class.
 */
public class GUI extends Application {

    private TextField cityField;
    private Label resultLabel;

    // Using TextArea instead of Label for forecast so multi-line text displays properly
    private TextArea forecastArea;

    private ListView<String> historyList;
    private ImageView weatherIcon;

    @Override
    public void start(Stage stage) {

        // Title at the top of the application
        Label title = new Label("Weather App");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Input field for city name
        cityField = new TextField();
        cityField.setPromptText("Enter city");

        // Dropdown for unit selection
        ComboBox<String> unitBox = new ComboBox<>();
        unitBox.getItems().addAll("Celsius", "Fahrenheit");
        unitBox.setValue("Celsius");

        // Button to trigger weather search
        Button searchButton = new Button("Get Weather");
        searchButton.setStyle(
                "-fx-background-color: #1E90FF;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;"
        );

        // Label for current weather results
        resultLabel = new Label();

        // Forecast display area (multi-line supported)
        forecastArea = new TextArea();
        forecastArea.setEditable(false);
        forecastArea.setWrapText(true);
        forecastArea.setPrefHeight(120);

        // Weather icon display
        weatherIcon = new ImageView();
        weatherIcon.setFitHeight(80);
        weatherIcon.setFitWidth(80);

        // Search history list
        historyList = new ListView<>();

        /*
         * When the button is clicked, this block executes.
         * It retrieves input, calls the API, and updates the UI.
         */
        searchButton.setOnAction(e -> {

            String city = cityField.getText().trim();
            String unit = unitBox.getValue();

            // Basic validation
            if (city.isEmpty()) {
                resultLabel.setText("Please enter a city name.");
                return;
            }

            resultLabel.setText("Loading...");

            WeatherData data = WeatherService.getWeather(city, unit);

            if (data != null && !data.getDescription().equals("INVALID_CITY")) {

                String tempUnit = unit.equals("Fahrenheit") ? "°F" : "°C";
                String windUnit = unit.equals("Fahrenheit") ? "mph" : "m/s";

                // Capitalize description for readability
                String desc = data.getDescription();
                desc = desc.substring(0, 1).toUpperCase() + desc.substring(1);

                resultLabel.setText(
                        "Temperature: " + data.getTemperature() + " " + tempUnit + "\n" +
                        "Humidity: " + data.getHumidity() + "%\n" +
                        "Wind Speed: " + data.getWindSpeed() + " " + windUnit + "\n" +
                        "Condition: " + desc
                );

                // Display weather icon
                String iconUrl = "http://openweathermap.org/img/wn/"
                        + data.getIconCode() + "@2x.png";
                weatherIcon.setImage(new Image(iconUrl));

                // Display forecast (this is now guaranteed to show properly)
                forecastArea.setText(WeatherService.getForecast(city));

                // Update history
                HistoryManager.addSearch(city);
                historyList.getItems().setAll(HistoryManager.getHistory());

            } else {
                resultLabel.setText("Invalid city name.");
                forecastArea.clear();
                weatherIcon.setImage(null);
            }
        });

        /*
         * Layout structure.
         * VBox stacks elements vertically with spacing.
         */
        VBox layout = new VBox(15,
                title,
                cityField,
                unitBox,
                searchButton,
                weatherIcon,
                resultLabel,
                new Label("Forecast:"),
                forecastArea,
                new Label("Search History:"),
                historyList
        );

        layout.setPadding(new Insets(20));

        /*
         * Dynamic background based on time of day.
         * This adds a simple visual change without complicating the UI.
         */
        int hour = 21;

        if (hour >= 6 && hour < 12) {
            layout.setStyle("-fx-background-color: linear-gradient(to bottom, #FFFAE3, #87CEEB);");
        } else if (hour >= 12 && hour < 18) {
            layout.setStyle("-fx-background-color: linear-gradient(to bottom, #87CEEB, #4682B4);");
        } else {
            layout.setStyle("-fx-background-color: linear-gradient(to bottom, #2C3E50, #000000); -fx-text-fill: white;");
        }

        Scene scene = new Scene(layout, 380, 520);

        stage.setTitle("Weather App");
        stage.setScene(scene);
        stage.show();
    }
}