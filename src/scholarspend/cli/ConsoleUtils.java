package scholarspend.cli;

import scholarspend.util.DateUtils;

import java.time.LocalDate;
import java.util.Scanner;

public class ConsoleUtils {

    private static final Scanner sc = new Scanner(System.in);

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a whole number.");
            }
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a valid number (e.g. 12.50).");
            }
        }
    }

    /** Prompts for a date in dd/MM/yyyy format. */
    public static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            LocalDate date = DateUtils.parse(input);
            if (date != null) return date;
            System.out.println("  Please use dd/MM/yyyy format (e.g. 20/03/2026).");
        }
    }

    /** Prompts for a month in MM/yyyy format, returns first day of that month. */
    public static LocalDate readMonth(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                String[] parts = input.split("/");
                int month = Integer.parseInt(parts[0].trim());
                int year  = Integer.parseInt(parts[1].trim());
                return LocalDate.of(year, month, 1);
            } catch (Exception e) {
                System.out.println("  Please use MM/yyyy format (e.g. 03/2026).");
            }
        }
    }

    public static void printSeparator() {
        System.out.println("-----------------------------------------------------");
    }

    public static void printHeader(String title) {
        System.out.println();
        printSeparator();
        System.out.println("  " + title);
        printSeparator();
    }

    public static void pressEnter() {
        System.out.print("\n  [Press ENTER to continue]");
        sc.nextLine();
    }
}
