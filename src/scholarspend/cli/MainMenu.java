package scholarspend.cli;

import scholarspend.model.User;
import scholarspend.model.UserRole;
import scholarspend.service.*;

public class MainMenu {

    private final UserService    userService;
    private final FinanceService financeService;
    private final ExportService  exportService;

    private final ExpenseMenu      expenseMenu;
    private final IncomeMenu       incomeMenu;
    private final SubscriptionMenu subscriptionMenu;
    private final MetricsMenu      metricsMenu;
    private final BudgetMenu       budgetMenu;
    private final GoalMenu         goalMenu;
    private final AdminMenu        adminMenu;

    public MainMenu(UserService us, FinanceService fs, ExportService es) {
        this.userService    = us;
        this.financeService = fs;
        this.exportService  = es;

        this.expenseMenu      = new ExpenseMenu(fs);
        this.incomeMenu       = new IncomeMenu(fs);
        this.subscriptionMenu = new SubscriptionMenu(fs);
        this.metricsMenu      = new MetricsMenu(fs);
        this.budgetMenu       = new BudgetMenu(fs);
        this.goalMenu         = new GoalMenu(fs);
        this.adminMenu        = new AdminMenu(us, fs);
    }

    public void run() {
        printBanner();
        User currentUser = null;

        while (currentUser == null) {
            ConsoleUtils.printHeader("WELCOME TO SCHOLARSPEND");
            System.out.println("  1. Log in");
            System.out.println("  2. Register");
            System.out.println("  0. Exit");
            String choice = ConsoleUtils.readLine("\n  Choose: ");
            switch (choice) {
                case "1": currentUser = login(); break;
                case "2": register(); break;
                case "0": System.out.println("  Goodbye!"); return;
                default:  System.out.println("  Invalid option."); break;
            }
        }

        boolean running = true;
        while (running) {
            ConsoleUtils.printHeader("MAIN MENU  --  " + currentUser.getUsername()
                    + " [" + currentUser.getRole() + "]");
            System.out.println("  1. Expenses");
            System.out.println("  2. Income");
            System.out.println("  3. Subscriptions");
            System.out.println("  4. Budgets");
            System.out.println("  5. Savings Goals");
            System.out.println("  6. Analytics & Metrics");
            System.out.println("  7. Export Report (CSV)");
            if (currentUser.getRole() != UserRole.NORMAL_USER)
                System.out.println("  8. Admin / Manager Panel");
            System.out.println("  0. Log out");
            String choice = ConsoleUtils.readLine("\n  Choose: ");

            switch (choice) {
                case "1": expenseMenu.show(currentUser); break;
                case "2": incomeMenu.show(currentUser); break;
                case "3": subscriptionMenu.show(currentUser); break;
                case "4": budgetMenu.show(currentUser); break;
                case "5": goalMenu.show(currentUser); break;
                case "6": metricsMenu.show(currentUser); break;
                case "7": {
                    String path = exportService.exportReport(currentUser.getUsername());
                    System.out.println(path != null ? "  Report saved to: " + path : "  Export failed.");
                    ConsoleUtils.pressEnter();
                    break;
                }
                case "8":
                    if (currentUser.getRole() != UserRole.NORMAL_USER)
                        adminMenu.show(currentUser);
                    else
                        System.out.println("  Invalid option.");
                    break;
                case "0":
                    System.out.println("  Logged out. Goodbye, " + currentUser.getUsername() + "!");
                    running = false;
                    break;
                default:
                    System.out.println("  Invalid option.");
                    break;
            }
        }
    }

    private User login() {
        ConsoleUtils.printHeader("LOG IN");
        String username = ConsoleUtils.readLine("  Username: ");
        String password = ConsoleUtils.readLine("  Password: ");
        java.util.Optional<User> result = userService.login(username, password);
        if (result.isPresent()) {
            System.out.println("  Welcome back, " + username + "!");
            ConsoleUtils.pressEnter();
            return result.get();
        }
        System.out.println("  Invalid credentials.");
        ConsoleUtils.pressEnter();
        return null;
    }

    private void register() {
        ConsoleUtils.printHeader("REGISTER");
        String username = ConsoleUtils.readLine("  Choose a username: ");
        String password = ConsoleUtils.readLine("  Choose a password: ");
        if (userService.register(username, password))
            System.out.println("  Account created! You can now log in.");
        else
            System.out.println("  Username already taken, try another.");
        ConsoleUtils.pressEnter();
    }

    private void printBanner() {
        System.out.println();
        System.out.println("  +==========================================+");
        System.out.println("  |         S C H O L A R S P E N D         |");
        System.out.println("  |    Student Financial Management Tool    |");
        System.out.println("  |              v2.0                       |");
        System.out.println("  +==========================================+");
    }
}
