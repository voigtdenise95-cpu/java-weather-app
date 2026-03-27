package com.weatherapp.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/*
 * Keeps track of what the user searched.
 * Nothing fancy — just a list with timestamps.
 */
public class HistoryManager {

    private static List<String> history = new ArrayList<>();

    public static void addSearch(String city) {

        // Format date so it looks human… not like a robot log file
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM HH:mm");

        history.add(city + " - " + LocalDateTime.now().format(formatter));
    }

    public static List<String> getHistory() {
        return history;
    }
}