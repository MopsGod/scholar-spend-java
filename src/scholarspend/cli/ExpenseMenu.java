package scholarspend.cli;

import scholarspend.model.Expense;
import scholarspend.model.ExpenseCategory;
import scholarspend.model.User;
import scholarspend.service.FinanceService;
import scholarspend.util.DateUtils;

import java.time.LocalDate;
import java.util.List;

public class ExpenseMenu {

    private final FinanceService fs;

    public ExpenseMenu(FinanceService fs) {
        this.fs = fs;
    }

    public void show(User user) {
        boolean back = false;
        while (!back) {
            ConsoleUtils.printHeader("EXPENSES");
            System.out.println("  1. View my expenses");
            System.out.println("  2. Add expense");
            System.out.println("  3. Edit expense");
            System.out.println("  4. Delete expense");
            System.out.println("  0. Back");
            String choice = ConsoleUtils.readLine("\n  Choose: ");
            switch (choice) {
                case "1": viewExpenses(user); break;
                case "2": addExpense(user); break;
                case "3": editExpense(user); break;
                case "4": deleteExpense(user); break;
                case "0": back = true; break;
                default:  System.out.println("  Invalid option."); break;
            }
        }
    }

    private void viewExpenses(User user) {
        List<Expense> list = fs.getExpenses(user.getUsername());
        ConsoleUtils.printHeader("MY EXPENSES");
        if (list.isEmpty()) {
            System.out.println("  No expenses recorded yet.");
        } else {
            System.out.printf("  %-6s %-25s %10s  %-16s  %s%n",
                    "ID", "Description", "Amount", "Category", "Date");
            ConsoleUtils.printSeparator();
            for (Expense e : list) System.out.println("  " + e);
            double total = list.stream().mapToDouble(Expense::getAmount).sum();
            ConsoleUtils.printSeparator();
            System.out.printf("  Total: EUR %.2f%n", total);
        }
        ConsoleUtils.pressEnter();
    }

    private void addExpense(User user) {
        ConsoleUtils.printHeader("ADD EXPENSE");
        String desc   = ConsoleUtils.readLine("  Description: ");
        double amount = ConsoleUtils.readDouble("  Amount (EUR): ");
        ExpenseCategory cat = pickCategory();
        LocalDate date = ConsoleUtils.readDate("  Date (dd/MM/yyyy): ");
        fs.addExpense(user.getUsername(), desc, amount, cat, date);
        System.out.println("  Expense added.");
        ConsoleUtils.pressEnter();
    }

    private void editExpense(User user) {
        viewExpenses(user);
        int id = ConsoleUtils.readInt("  Enter ID to edit: ");
        ConsoleUtils.printHeader("EDIT EXPENSE");
        String desc   = ConsoleUtils.readLine("  New description: ");
        double amount = ConsoleUtils.readDouble("  New amount (EUR): ");
        ExpenseCategory cat = pickCategory();
        LocalDate date = ConsoleUtils.readDate("  New date (dd/MM/yyyy): ");
        System.out.println(fs.editExpense(user.getUsername(), id, desc, amount, cat, date)
                ? "  Expense updated." : "  Not found.");
        ConsoleUtils.pressEnter();
    }

    private void deleteExpense(User user) {
        viewExpenses(user);
        int id = ConsoleUtils.readInt("  Enter ID to delete: ");
        System.out.println(fs.deleteExpense(user.getUsername(), id)
                ? "  Expense deleted." : "  Not found.");
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
