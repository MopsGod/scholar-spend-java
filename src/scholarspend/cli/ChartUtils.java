package scholarspend.cli;

import scholarspend.model.ExpenseCategory;

import java.util.Map;

/**
 * ASCII bar chart utilities for the console.
 */
public class ChartUtils {

    private static final int BAR_WIDTH = 30; // max bar length in characters

    /**
     * Prints a horizontal ASCII bar chart of expenses by category.
     *
     * Example output:
     *   FOOD            ############          EUR 80.00  (45%)
     *   TRANSPORT       ######                EUR 30.00  (17%)
     *   ENTERTAINMENT   ####                  EUR 13.99  (8%)
     */
    public static void printExpenseChart(Map<ExpenseCategory, Double> data) {
        double total = data.values().stream().mapToDouble(Double::doubleValue).sum();
        if (total == 0) {
            System.out.println("  No expenses to chart.");
            return;
        }

        double max = data.values().stream().mapToDouble(Double::doubleValue).max().orElse(1.0);

        System.out.println();
        System.out.printf("  %-18s  %-32s  %10s  %s%n", "CATEGORY", "BAR", "AMOUNT", "SHARE");
        ConsoleUtils.printSeparator();

        for (Map.Entry<ExpenseCategory, Double> entry : data.entrySet()) {
            double amount = entry.getValue();
            if (amount == 0) continue;

            int bars   = (int) Math.round((amount / max) * BAR_WIDTH);
            int pct    = (int) Math.round((amount / total) * 100.0);
            String bar = repeat("#", bars) + repeat(" ", BAR_WIDTH - bars);

            System.out.printf("  %-18s  [%s]  EUR%7.2f  %3d%%%n",
                    entry.getKey(), bar, amount, pct);
        }
        ConsoleUtils.printSeparator();
        System.out.printf("  %-18s  %-32s  EUR%7.2f%n", "TOTAL", "", total);
    }

    /**
     * Prints a mini bar chart for a single metric (e.g. goal progress).
     * fillChar is repeated for filled portion, emptyChar for empty.
     */
    public static String progressBar(double percent, int width) {
        int filled = (int) Math.round((percent / 100.0) * width);
        filled = Math.min(filled, width);
        return "[" + repeat("=", filled) + repeat("-", width - filled) + "]";
    }

    private static String repeat(String ch, int times) {
        if (times <= 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) sb.append(ch);
        return sb.toString();
    }
}
