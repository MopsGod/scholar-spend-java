package scholarspend.cli;

import scholarspend.model.ExpenseCategory;
import scholarspend.model.User;
import scholarspend.service.FinanceService;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MetricsMenu {

    private final FinanceService fs;
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("MMM yyyy");

    public MetricsMenu(FinanceService fs) {
        this.fs = fs;
    }

    public void show(User user) {
        boolean back = false;
        while (!back) {
            ConsoleUtils.printHeader("ANALYTICS & METRICS");
            System.out.println("  1. Financial Health Dashboard");
            System.out.println("  2. Expense Chart (by category)");
            System.out.println("  3. Monthly Summary & Trend");
            System.out.println("  4. Spending Prediction (next month)");
            System.out.println("  0. Back");
            String choice = ConsoleUtils.readLine("\n  Choose: ");
            switch (choice) {
                case "1": showDashboard(user); break;
                case "2": showExpenseChart(user); break;
                case "3": showMonthlySummary(user); break;
                case "4": showPrediction(user); break;
                case "0": back = true; break;
                default:  System.out.println("  Invalid option."); break;
            }
        }
    }

    // -------------------------------------------------------------------------
    private void showDashboard(User user) {
        String owner = user.getUsername();
        ConsoleUtils.printHeader("FINANCIAL HEALTH DASHBOARD");

        double balance = fs.getBalance(owner);
        double fts     = fs.funToStudyIndex(owner);
        double sfi     = fs.subscriptionFatigueIndex(owner);
        double sm      = fs.survivalMetric(owner);

        // Balance
        System.out.printf("  Current Balance:  EUR %.2f%n", balance);
        if (balance < 0)
            System.out.println("  [!!] ALERT: You are in deficit!");
        System.out.println();

        // Survival Metric — check first for alert
        System.out.println("  --- Survival Metric ---");
        System.out.println("  (balance / total essential expenses)");
        if (sm == Double.MAX_VALUE) {
            System.out.println("  No essential expenses recorded yet.");
        } else {
            System.out.printf("  Index: %.2f%n", sm);
            if (sm < 1.0)
                System.out.println("  [!!] WARNING: Low funds! Balance does not cover essential costs!");
            else
                System.out.printf("  [OK] Balance covers ~%.1f months of essentials.%n", sm);
        }
        System.out.println();

        // Fun-to-Study
        System.out.println("  --- Fun-to-Study Index ---");
        System.out.println("  (leisure spending / academic spending)");
        if (fts == Double.MAX_VALUE)
            System.out.println("  No academic spending recorded yet.");
        else {
            System.out.printf("  Index: %.2f%n", fts);
            if      (fts > 2.0) System.out.println("  [!] You spend 2x+ more on fun than on studies!");
            else if (fts > 1.0) System.out.println("  More on leisure than study materials.");
            else if (fts > 0.0) System.out.println("  [OK] Study spending leads.");
            else                System.out.println("  No leisure expenses recorded.");
        }
        System.out.println();

        // Subscription Fatigue
        System.out.println("  --- Subscription Fatigue Index ---");
        System.out.println("  (active subscriptions / total income * 100)");
        System.out.printf("  Index: %.1f%% of income goes to subscriptions%n", sfi);
        if      (sfi > 20.0) System.out.println("  [!] Subscription fatigue! Consider cancelling some.");
        else if (sfi > 10.0) System.out.println("  Moderate cost - keep an eye on it.");
        else                 System.out.println("  [OK] Healthy subscription spending.");

        // Budget overspend alerts
        System.out.println();
        System.out.println("  --- Budget Alerts (current month) ---");
        YearMonth now = YearMonth.now();
        boolean anyAlert = false;
        for (ExpenseCategory cat : ExpenseCategory.values()) {
            double over = fs.overspend(owner, cat, now.atDay(1));
            if (over > 0) {
                System.out.printf("  [!] OVER BUDGET: %-16s  EUR +%.2f over limit%n", cat, over);
                anyAlert = true;
            }
        }
        if (!anyAlert) System.out.println("  All budgets within limits this month.");

        ConsoleUtils.pressEnter();
    }

    // -------------------------------------------------------------------------
    private void showExpenseChart(User user) {
        ConsoleUtils.printHeader("EXPENSE CHART  (all time, by category)");
        Map<ExpenseCategory, Double> data = fs.expensesByCategoryAllTime(user.getUsername());
        ChartUtils.printExpenseChart(data);
        ConsoleUtils.pressEnter();
    }

    // -------------------------------------------------------------------------
    private void showMonthlySummary(User user) {
        ConsoleUtils.printHeader("MONTHLY SUMMARY");
        Map<YearMonth, Double> monthly = fs.monthlyBalance(user.getUsername());

        if (monthly.isEmpty()) {
            System.out.println("  No data yet.");
            ConsoleUtils.pressEnter();
            return;
        }

        List<Map.Entry<YearMonth, Double>> entries = new ArrayList<>(monthly.entrySet());

        System.out.printf("  %-12s  %12s  %s%n", "MONTH", "NET BALANCE", "TREND");
        ConsoleUtils.printSeparator();

        for (int i = 0; i < entries.size(); i++) {
            YearMonth ym  = entries.get(i).getKey();
            double    bal = entries.get(i).getValue();
            String trend  = "";

            if (i > 0) {
                double prev = entries.get(i - 1).getValue();
                if (prev != 0) {
                    double pct = ((bal - prev) / Math.abs(prev)) * 100.0;
                    trend = String.format("%s %.1f%% vs %s",
                            pct >= 0 ? "+" : "", pct,
                            entries.get(i - 1).getKey().format(MONTH_FMT));
                }
            }

            System.out.printf("  %-12s  EUR %8.2f  %s%n",
                    ym.format(MONTH_FMT), bal, trend);
        }

        ConsoleUtils.pressEnter();
    }

    // -------------------------------------------------------------------------
    private void showPrediction(User user) {
        ConsoleUtils.printHeader("SPENDING PREDICTION");
        double predicted = fs.predictNextMonth(user.getUsername(), 3);
        double income    = 0;
        try {
            income = fs.getIncome(user.getUsername()).stream()
                    .mapToDouble(i -> i.getAmount()).sum();
        } catch (Exception ignored) {}

        System.out.println("  Based on linear regression of your last 3 months of expenses:");
        System.out.println();
        System.out.printf("  Predicted spending next month:  EUR %.2f%n", predicted);

        if (income > 0) {
            double remaining = income - predicted;
            System.out.printf("  Estimated remaining from income: EUR %.2f%n", remaining);
            if (remaining < 0)
                System.out.println("  [!!] WARNING: Predicted spending EXCEEDS your recorded income!");
        }

        ConsoleUtils.pressEnter();
    }
}
