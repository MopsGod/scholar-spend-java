package scholarspend.service;

import scholarspend.model.*;
import scholarspend.util.DateUtils;

import java.io.*;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ExportService {

    private final FinanceService fs;

    public ExportService(FinanceService fs) {
        this.fs = fs;
    }

    public String exportReport(String owner) {
        String ts   = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String path = "data/report_" + owner + "_" + ts + ".csv";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            line(bw, "ScholarSpend Financial Report");
            line(bw, "User: " + owner);
            line(bw, "Generated: " + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            line(bw, "");

            // Income
            line(bw, "=== INCOME ===");
            line(bw, "ID,Source,Amount,Date");
            List<Income> incomeList = fs.getIncome(owner);
            for (Income i : incomeList)
                line(bw, i.getId() + "," + i.getSource() + ","
                        + i.getAmount() + "," + DateUtils.format(i.getDate()));
            double totalIncome = incomeList.stream().mapToDouble(Income::getAmount).sum();
            line(bw, "TOTAL,,,EUR " + String.format("%.2f", totalIncome));
            line(bw, "");

            // Expenses
            line(bw, "=== EXPENSES ===");
            line(bw, "ID,Description,Amount,Category,Date");
            List<Expense> expList = fs.getExpenses(owner);
            for (Expense e : expList)
                line(bw, e.getId() + "," + e.getDescription() + ","
                        + e.getAmount() + "," + e.getCategory()
                        + "," + DateUtils.format(e.getDate()));
            double totalExpenses = expList.stream().mapToDouble(Expense::getAmount).sum();
            line(bw, "TOTAL,,EUR " + String.format("%.2f", totalExpenses) + ",,");
            line(bw, "");

            // Subscriptions
            line(bw, "=== SUBSCRIPTIONS ===");
            line(bw, "ID,Name,Monthly Cost,Status");
            for (Subscription s : fs.getSubscriptions(owner))
                line(bw, s.getId() + "," + s.getName() + ","
                        + s.getMonthlyCost() + "," + (s.isActive() ? "ACTIVE" : "INACTIVE"));
            line(bw, "");

            // Budgets
            line(bw, "=== BUDGETS ===");
            line(bw, "ID,Category,Monthly Limit,Month");
            for (Budget b : fs.getBudgets(owner))
                line(bw, b.getId() + "," + b.getCategory() + ","
                        + b.getMonthlyLimit() + "," + DateUtils.format(b.getMonth()));
            line(bw, "");

            // Goals
            line(bw, "=== SAVINGS GOALS ===");
            line(bw, "ID,Name,Target,Current,Progress%,Deadline");
            for (Goal g : fs.getGoals(owner))
                line(bw, g.getId() + "," + g.getName() + ","
                        + g.getTargetAmount() + "," + g.getCurrentAmount() + ","
                        + String.format("%.1f", g.progressPercent()) + "%,"
                        + DateUtils.format(g.getDeadline()));
            line(bw, "");

            // Monthly summary
            line(bw, "=== MONTHLY NET BALANCE ===");
            line(bw, "Month,Net Balance");
            Map<YearMonth, Double> monthly = fs.monthlyBalance(owner);
            for (Map.Entry<YearMonth, Double> entry : monthly.entrySet())
                line(bw, entry.getKey().toString() + ",EUR " + String.format("%.2f", entry.getValue()));
            line(bw, "");

            // Metrics
            line(bw, "=== FINANCIAL METRICS ===");
            line(bw, "Metric,Value");
            double fts = fs.funToStudyIndex(owner);
            double sfi = fs.subscriptionFatigueIndex(owner);
            double sm  = fs.survivalMetric(owner);
            double bal = fs.getBalance(owner);
            double pred = fs.predictNextMonth(owner, 3);
            line(bw, "Balance,EUR " + String.format("%.2f", bal));
            line(bw, "Fun-to-Study Index," + (fts == Double.MAX_VALUE ? "N/A" : String.format("%.2f", fts)));
            line(bw, "Subscription Fatigue Index," + String.format("%.1f%%", sfi));
            line(bw, "Survival Metric," + (sm == Double.MAX_VALUE ? "N/A" : String.format("%.2f", sm)));
            line(bw, "Predicted Next Month Spending,EUR " + String.format("%.2f", pred));

        } catch (IOException e) {
            System.err.println("Export failed: " + e.getMessage());
            return null;
        }
        return path;
    }

    private void line(BufferedWriter bw, String text) throws IOException {
        bw.write(text);
        bw.newLine();
    }
}
