package scholarspend.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Central date formatting utility.
 * All display and user input uses dd/MM/yyyy.
 * CSV storage still uses ISO (yyyy-MM-dd) for reliable parsing.
 */
public class DateUtils {

    public static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter CSV     = DateTimeFormatter.ISO_LOCAL_DATE; // yyyy-MM-dd

    /** Format a date for display to the user. */
    public static String format(LocalDate date) {
        return date.format(DISPLAY);
    }

    /**
     * Parse a date entered by the user (dd/MM/yyyy).
     * Returns null if input is invalid.
     */
    public static LocalDate parse(String input) {
        try {
            return LocalDate.parse(input.trim(), DISPLAY);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
