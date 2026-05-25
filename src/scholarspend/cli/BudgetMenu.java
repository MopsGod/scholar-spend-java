package scholarspend.cli;

import scholarspend.model.Budget;
import scholarspend.model.ExpenseCategory;
import scholarspend.model.User;
import scholarspend.service.FinanceService;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class BudgetMenu {

    private final FinanceService fs;

    public BudgetMenu(FinanceService fs) {
        this.fs = fs;
    }

    public void show(User user) {
        boolean back = false;
        while (!back) {
            ConsoleUtils.printHeader("BUDGETS");
            System.out.println("  1. View my budgets");
            System.out.println("  2. Set a new budget");
            System.out.println("  3. Delete a budget");
            System.out.println("  0. Back");
            String choice = ConsoleUtils.readLine("\n  Choose: ");
            switch (choice) {
                case "1": viewBudgets(user); break;
                case "2": addBudget(user); break;
                case "3": deleteBudget(user); break;
                case "0": back = true; break;
                default:  System.out.println("  Invalid option."); break;
            }
        }
    }

    private void viewBudgets(User user) {
        List<Budget> list = fs.getBudgets(user.getUsername());
        ConsoleUtils.printHeader("MY BUDGETS");
        if (list.isEmpty()) {
            System.out.println("  No budgets set yet.");
        } else {
            System.out.printf("  %-6s %-16s  %12s  %12s  %12s%n",
                    "ID", "CATEGORY", "LIMIT", "SPENT", "STATUS");
            ConsoleUtils.printSeparator();
            for (Budget b : list) {
                YearMonth ym = YearMonth.from(b.getMonth());
                double spent = 0;
                for (var e : fs.getExpenses(user.getUsername())) {
                    if (e.getCategory() == b.getCategory()
                            && YearMonth.from(e.getDate()).equals(ym)) {
                        spent += e.getAmount();
                    }
                }
                double over = spent - b.getMonthlyLimit();
                String status = over > 0
                        ? String.format("[!!] OVER by EUR %.2f", over)
                        : String.format("[OK] EUR %.2f left", -over);

                System.out.printf("  %-6d %-16s  EUR%7.2f  EUR%7.2f  %s%n",
                        b.getId(), b.getCategory(),
                        b.getMonthlyLimit(), spent, status);
            }
        }
        ConsoleUtils.pressEnter();
    }

    private void addBudget(User user) {
        ConsoleUtils.printHeader("SET BUDGET");
        ExpenseCategory cat = pickCategory();
        double limit = ConsoleUtils.readDouble("  Monthly limit (EUR): ");
        LocalDate month = ConsoleUtils.readMonth("  Month (MM/yyyy, e.g. 04/2026): ");
        fs.addBudget(user.getUsername(), cat, limit, month);
        System.out.println("  Budget set.");
        ConsoleUtils.pressEnter();
    }

    private void deleteBudget(User user) {
        viewBudgets(user);
        int id = ConsoleUtils.readInt("  Enter ID to delete: ");
        System.out.println(fs.deleteBudget(user.getUsername(), id) ? "  Budget deleted." : "  Not found.");
        ConsoleUtils.pressEnter();
    }

    private ExpenseCategory pickCategory() {
        ExpenseCategory[] cats = ExpenseCategory.values();
        System.out.println("  Categories:");
        for (int i = 0; i < cats.length; i++)
            System.out.printf("    %d. %s%n", i + 1, cats[i]);
        while (true) {
            int c = ConsoleUtils.readInt("  Pick (1-" + cats.length + "): ");
            if (c >= 1 && c <= cats.length) return cats[c - 1];
            System.out.println("  Out of range.");
        }
    }
}
