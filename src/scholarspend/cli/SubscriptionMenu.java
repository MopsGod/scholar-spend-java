package scholarspend.cli;

import scholarspend.model.Subscription;
import scholarspend.model.User;
import scholarspend.service.FinanceService;

import java.util.List;

public class SubscriptionMenu {

    private final FinanceService fs;

    public SubscriptionMenu(FinanceService fs) {
        this.fs = fs;
    }

    public void show(User user) {
        boolean back = false;
        while (!back) {
            ConsoleUtils.printHeader("SUBSCRIPTIONS");
            System.out.println("  1. View my subscriptions");
            System.out.println("  2. Add subscription");
            System.out.println("  3. Edit subscription");
            System.out.println("  4. Delete subscription");
            System.out.println("  0. Back");
            String choice = ConsoleUtils.readLine("\n  Choose: ");
            switch (choice) {
                case "1": viewSubs(user); break;
                case "2": addSub(user); break;
                case "3": editSub(user); break;
                case "4": deleteSub(user); break;
                case "0": back = true; break;
                default:  System.out.println("  Invalid option."); break;
            }
        }
    }

    private void viewSubs(User user) {
        List<Subscription> list = fs.getSubscriptions(user.getUsername());
        ConsoleUtils.printHeader("MY SUBSCRIPTIONS");
        if (list.isEmpty()) {
            System.out.println("  No subscriptions recorded yet.");
        } else {
            for (Subscription s : list) System.out.println("  " + s);
            double total = list.stream().filter(Subscription::isActive)
                    .mapToDouble(Subscription::getMonthlyCost).sum();
            ConsoleUtils.printSeparator();
            System.out.printf("  Active total: EUR %.2f/month%n", total);
        }
        ConsoleUtils.pressEnter();
    }

    private void addSub(User user) {
        ConsoleUtils.printHeader("ADD SUBSCRIPTION");
        String name = ConsoleUtils.readLine("  Name (e.g. Netflix, Spotify): ");
        double cost = ConsoleUtils.readDouble("  Monthly cost (EUR): ");
        fs.addSubscription(user.getUsername(), name, cost);
        System.out.println("  Subscription added.");
        ConsoleUtils.pressEnter();
    }

    private void editSub(User user) {
        viewSubs(user);
        int id = ConsoleUtils.readInt("  Enter ID to edit: ");
        ConsoleUtils.printHeader("EDIT SUBSCRIPTION");
        String name   = ConsoleUtils.readLine("  New name: ");
        double cost   = ConsoleUtils.readDouble("  New monthly cost (EUR): ");
        String active = ConsoleUtils.readLine("  Active? (y/n): ");
        System.out.println(fs.editSubscription(user.getUsername(), id, name, cost,
                active.equalsIgnoreCase("y"))
                ? "  Subscription updated." : "  Not found.");
        ConsoleUtils.pressEnter();
    }

    private void deleteSub(User user) {
        viewSubs(user);
        int id = ConsoleUtils.readInt("  Enter ID to delete: ");
        System.out.println(fs.deleteSubscription(user.getUsername(), id)
                ? "  Subscription deleted." : "  Not found.");
        ConsoleUtils.pressEnter();
    }
}
