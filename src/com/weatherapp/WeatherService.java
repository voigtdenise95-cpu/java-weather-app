package com.weatherapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONObject;

/*
 * This class is responsible for communicating with the OpenWeatherMap API.
 * It retrieves current weather data and forecast data, and converts
 * the JSON responses into usable Java objects or formatted strings.
 */
public class WeatherService {

    private static final String API_KEY = "3c739f5daa697b3a8fbb848ce44f30b0";

    /*
     * Retrieves current weather data for a specified city and unit system.
     */
    public static WeatherData getWeather(String city, String unit) {
        try {
            String units = unit.equals("Fahrenheit") ? "imperial" : "metric";

            // Encode city name to support spaces and special characters
            String encodedCity = URLEncoder.encode(city, "UTF-8");

            String urlString = "https://api.openweathermap.org/data/2.5/weather?q="
                    + encodedCity + "&appid=" + API_KEY + "&units=" + units;

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();

            // If the city is not found, return a marker value
            if (responseCode == 404) {
                return new WeatherData(-1, -1, -1, "INVALID_CITY", "");
            }

            // Handle other API errors
            if (responseCode != 200) {
                System.out.println("API Error Code: " + responseCode);
                return null;
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            JSONObject json = new JSONObject(response.toString());

            double temp = json.getJSONObject("main").getDouble("temp");
            int humidity = json.getJSONObject("main").getInt("humidity");
            double wind = json.getJSONObject("wind").getDouble("speed");

            String desc = json.getJSONArray("weather")
                              .getJSONObject(0)
                              .getString("description");

            String icon = json.getJSONArray("weather")
                              .getJSONObject(0)
                              .getString("icon");

            return new WeatherData(temp, humidity, wind, desc, icon);

        } catch (Exception e) {
            System.out.println("API Exception: " + e.getMessage());
            return null;
        }
    }

    /*
     * Retrieves a short-term forecast for a specified city.
     * The forecast data is returned as a formatted string.
     */
    public static String getForecast(String city) {
        try {
            String encodedCity = URLEncoder.encode(city, "UTF-8");

            String urlString = "https://api.openweathermap.org/data/2.5/forecast?q="
                    + encodedCity + "&appid=" + API_KEY + "&units=metric";

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();

            // If the request fails, return a message
            if (responseCode != 200) {
                System.out.println("Forecast API Error Code: " + responseCode);
                return "Forecast unavailable.";
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            JSONObject json = new JSONObject(response.toString());

            // Ensure forecast data exists
            if (!json.has("list")) {
                return "No forecast data available.";
            }

            StringBuilder forecast = new StringBuilder();

            /*
             * The API returns data in 3-hour intervals.
             * This loop extracts the first few entries for display.
             */
            int count = Math.min(5, json.getJSONArray("list").length());

            for (int i = 0; i < count; i++) {

                JSONObject item = json.getJSONArray("list").getJSONObject(i);

                double temp = item.getJSONObject("main").getDouble("temp");
                String time = item.getString("dt_txt");

                forecast.append(time)
                        .append(" - ")
                        .append(temp)
                        .append("°C\n");
            }

            // Ensure the result is not empty
            if (forecast.length() == 0) {
                return "Forecast unavailable.";
            }

            return forecast.toString();

        } catch (Exception e) {
            System.out.println("Forecast Exception: " + e.getMessage());
            return "Forecast unavailable.";
        }
    }
}