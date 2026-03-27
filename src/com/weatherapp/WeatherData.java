package com.weatherapp;

/*
 * Think of this class as a neat little container.
 * It just holds all the weather info we fetch from the API.
 * Nothing fancy — just clean data storage.
 */
public class WeatherData {

    private double temperature;
    private int humidity;
    private double windSpeed;
    private String description;
    private String iconCode;

    // Constructor — builds the object with all the data we care about
    public WeatherData(double temperature, int humidity, double windSpeed, String description, String iconCode) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.description = description;
        this.iconCode = iconCode;
    }

    // Getters (just returning values — no drama here)
    public double getTemperature() { return temperature; }
    public int getHumidity() { return humidity; }
    public double getWindSpeed() { return windSpeed; }
    public String getDescription() { return description; }
    public String getIconCode() { return iconCode; }
}