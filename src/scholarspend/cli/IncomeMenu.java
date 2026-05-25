package scholarspend.cli;

import scholarspend.model.Income;
import scholarspend.model.User;
import scholarspend.service.FinanceService;

import java.time.LocalDate;
import java.util.List;

public class IncomeMenu {

    private final FinanceService fs;

    public IncomeMenu(FinanceService fs) {
        this.fs = fs;
    }

    public void show(User user) {
        boolean back = false;
        while (!back) {
            ConsoleUtils.printHeader("INCOME");
            System.out.println("  1. View my income");
            System.out.println("  2. Add income");
            System.out.println("  3. Edit income");
            System.out.println("  4. Delete income");
            System.out.println("  0. Back");
            String choice = ConsoleUtils.readLine("\n  Choose: ");
            switch (choice) {
                case "1": viewIncome(user); break;
                case "2": addIncome(user); break;
                case "3": editIncome(user); break;
                case "4": deleteIncome(user); break;
                case "0": back = true; break;
                default:  System.out.println("  Invalid option."); break;
            }
        }
    }

    private void viewIncome(User user) {
        List<Income> list = fs.getIncome(user.getUsername());
        ConsoleUtils.printHeader("MY INCOME");
        if (list.isEmpty()) {
            System.out.println("  No income recorded yet.");
        } else {
            System.out.printf("  %-6s %-25s %10s  %s%n", "ID", "Source", "Amount", "Date");
            ConsoleUtils.printSeparator();
            for (Income i : list) System.out.println("  " + i);
            double total = list.stream().mapToDouble(Income::getAmount).sum();
            ConsoleUtils.printSeparator();
            System.out.printf("  Total: EUR %.2f%n", total);
        }
        ConsoleUtils.pressEnter();
    }

    private void addIncome(User user) {
        ConsoleUtils.printHeader("ADD INCOME");
        String source = ConsoleUtils.readLine("  Source (e.g. scholarship, part-time job, family): ");
        double amount = ConsoleUtils.readDouble("  Amount (EUR): ");
        LocalDate date = ConsoleUtils.readDate("  Date (dd/MM/yyyy): ");
        fs.addIncome(user.getUsername(), source, amount, date);
        System.out.println("  Income added.");
        ConsoleUtils.pressEnter();
    }

    private void editIncome(User user) {
        viewIncome(user);
        int id = ConsoleUtils.readInt("  Enter ID to edit: ");
        ConsoleUtils.printHeader("EDIT INCOME");
        String source = ConsoleUtils.readLine("  New source: ");
        double amount = ConsoleUtils.readDouble("  New amount (EUR): ");
        LocalDate date = ConsoleUtils.readDate("  New date (dd/MM/yyyy): ");
        System.out.println(fs.editIncome(user.getUsername(), id, source, amount, date)
                ? "  Income updated." : "  Not found.");
        ConsoleUtils.pressEnter();
    }

    private void deleteIncome(User user) {
        viewIncome(user);
        int id = ConsoleUtils.readInt("  Enter ID to delete: ");
        System.out.println(fs.deleteIncome(user.getUsername(), id)
                ? "  Income deleted." : "  Not found.");
        ConsoleUtils.pressEnter();
    }
}
