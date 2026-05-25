package scholarspend.cli;

import scholarspend.model.Goal;
import scholarspend.model.User;
import scholarspend.service.FinanceService;

import java.time.LocalDate;
import java.util.List;

public class GoalMenu {

    private final FinanceService fs;

    public GoalMenu(FinanceService fs) {
        this.fs = fs;
    }

    public void show(User user) {
        boolean back = false;
        while (!back) {
            ConsoleUtils.printHeader("SAVINGS GOALS");
            System.out.println("  1. View my goals");
            System.out.println("  2. Add a new goal");
            System.out.println("  3. Add savings to a goal");
            System.out.println("  4. Delete a goal");
            System.out.println("  0. Back");
            String choice = ConsoleUtils.readLine("\n  Choose: ");
            switch (choice) {
                case "1": viewGoals(user); break;
                case "2": addGoal(user); break;
                case "3": addSavings(user); break;
                case "4": deleteGoal(user); break;
                case "0": back = true; break;
                default:  System.out.println("  Invalid option."); break;
            }
        }
    }

    private void viewGoals(User user) {
        List<Goal> list = fs.getGoals(user.getUsername());
        ConsoleUtils.printHeader("MY SAVINGS GOALS");
        if (list.isEmpty()) {
            System.out.println("  No goals set yet.");
        } else {
            for (Goal g : list) {
                System.out.println("  " + g);
                String bar = ChartUtils.progressBar(g.progressPercent(), 30);
                System.out.printf("  Progress: %s %.1f%%%n", bar, g.progressPercent());
                if (g.isComplete()) System.out.println("  [!!] GOAL REACHED!");
                System.out.println();
            }
        }
        ConsoleUtils.pressEnter();
    }

    private void addGoal(User user) {
        ConsoleUtils.printHeader("NEW SAVINGS GOAL");
        String name    = ConsoleUtils.readLine("  Goal name (e.g. New GPU, Trip to Japan): ");
        double target  = ConsoleUtils.readDouble("  Target amount (EUR): ");
        LocalDate dl   = ConsoleUtils.readDate("  Deadline (dd/MM/yyyy): ");
        fs.addGoal(user.getUsername(), name, target, dl);
        System.out.println("  Goal created.");
        ConsoleUtils.pressEnter();
    }

    private void addSavings(User user) {
        viewGoals(user);
        int id = ConsoleUtils.readInt("  Enter goal ID to add savings to: ");
        double amount = ConsoleUtils.readDouble("  Amount to add (EUR): ");
        boolean ok = fs.addToGoal(user.getUsername(), id, amount);
        if (ok) {
            List<Goal> goals = fs.getGoals(user.getUsername());
            for (Goal g : goals) {
                if (g.getId() == id) {
                    System.out.printf("  Saved! Progress: %.1f%% (EUR %.2f / EUR %.2f)%n",
                            g.progressPercent(), g.getCurrentAmount(), g.getTargetAmount());
                    if (g.isComplete()) System.out.println("  [!!] Congratulations! Goal reached!");
                    break;
                }
            }
        } else {
            System.out.println("  Goal not found.");
        }
        ConsoleUtils.pressEnter();
    }

    private void deleteGoal(User user) {
        viewGoals(user);
        int id = ConsoleUtils.readInt("  Enter ID to delete: ");
        System.out.println(fs.deleteGoal(user.getUsername(), id) ? "  Goal deleted." : "  Not found.");
        ConsoleUtils.pressEnter();
    }
}
