package com.campusplacement.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeFormatter {
    private static final ZoneId UTC_ZONE = ZoneId.of("UTC");
    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");

    /**
     * Formats a LocalDateTime to IST timezone in 12-hour format with AM/PM
     * Assumes LocalDateTime is stored in UTC (common practice)
     * Example: "10:30 AM" or "02:15 PM"
     */
    public static String formatTimeToIST(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }
        // Assume LocalDateTime is in UTC, convert to IST
        ZonedDateTime utcDateTime = dateTime.atZone(UTC_ZONE);
        ZonedDateTime istDateTime = utcDateTime.withZoneSameInstant(IST_ZONE);
        return istDateTime.format(TIME_FORMATTER);
    }

    /**
     * Formats a LocalDateTime to IST timezone with date and time in 12-hour format
     * Assumes LocalDateTime is stored in UTC (common practice)
     * Example: "Jan 15, 2024 10:30 AM"
     */
    public static String formatDateTimeToIST(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }
        // Assume LocalDateTime is in UTC, convert to IST
        ZonedDateTime utcDateTime = dateTime.atZone(UTC_ZONE);
        ZonedDateTime istDateTime = utcDateTime.withZoneSameInstant(IST_ZONE);
        return istDateTime.format(DATE_TIME_FORMATTER);
    }

    /**
     * Formats time taken in seconds to mm:ss format
     */
    public static String formatTimeTaken(Long timeTakenSeconds) {
        if (timeTakenSeconds == null || timeTakenSeconds < 0) {
            return "N/A";
        }
        long minutes = timeTakenSeconds / 60;
        long seconds = timeTakenSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
