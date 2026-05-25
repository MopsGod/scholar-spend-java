package scholarspend.cli;

import scholarspend.model.*;
import scholarspend.service.FinanceService;
import scholarspend.service.UserService;

import java.util.List;

public class AdminMenu {

    private final UserService userService;
    private final FinanceService financeService;

    public AdminMenu(UserService userService, FinanceService financeService) {
        this.userService    = userService;
        this.financeService = financeService;
    }

    public void show(User currentUser) {
        if (currentUser.getRole() == UserRole.NORMAL_USER) {
            System.out.println("  Access denied.");
            return;
        }

        boolean back = false;
        while (!back) {
            ConsoleUtils.printHeader("ADMIN / MANAGER PANEL  [" + currentUser.getRole() + "]");
            System.out.println("  1. List all users");
            System.out.println("  2. Delete a user");
            if (currentUser.getRole() == UserRole.ADMIN) {
                System.out.println("  3. Change user role");
                System.out.println("  4. View all expenses (all users)");
                System.out.println("  5. View all income (all users)");
                System.out.println("  6. View all subscriptions (all users)");
            }
            System.out.println("  0. Back");
            String choice = ConsoleUtils.readLine("\n  Choose: ");

            switch (choice) {
                case "1":
                    listUsers();
                    break;
                case "2":
                    deleteUser(currentUser);
                    break;
                case "3":
                    if (currentUser.getRole() == UserRole.ADMIN) changeRole();
                    break;
                case "4":
                    if (currentUser.getRole() == UserRole.ADMIN) viewAllExpenses();
                    break;
                case "5":
                    if (currentUser.getRole() == UserRole.ADMIN) viewAllIncome();
                    break;
                case "6":
                    if (currentUser.getRole() == UserRole.ADMIN) viewAllSubs();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("  Invalid option.");
                    break;
            }
        }
    }

    private void listUsers() {
        ConsoleUtils.printHeader("ALL USERS");
        List<User> users = userService.getAllUsers();
        System.out.printf("  %-20s %-15s%n", "Username", "Role");
        ConsoleUtils.printSeparator();
        for (User u : users) {
            System.out.printf("  %-20s %-15s%n", u.getUsername(), u.getRole());
        }
        ConsoleUtils.pressEnter();
    }

    private void deleteUser(User admin) {
        listUsers();
        String username = ConsoleUtils.readLine("  Username to delete (cannot delete yourself): ");
        if (username.equalsIgnoreCase(admin.getUsername())) {
            System.out.println("  Cannot delete your own account.");
        } else {
            boolean ok = userService.deleteUser(username);
            System.out.println(ok ? "  User deleted." : "  User not found.");
        }
        ConsoleUtils.pressEnter();
    }

    private void changeRole() {
        listUsers();
        String username = ConsoleUtils.readLine("  Username to change: ");
        System.out.println("  Roles: 1. NORMAL_USER  2. MANAGER  3. ADMIN");
        int choice = ConsoleUtils.readInt("  New role: ");
        UserRole newRole;
        switch (choice) {
            case 1:  newRole = UserRole.NORMAL_USER; break;
            case 2:  newRole = UserRole.MANAGER; break;
            case 3:  newRole = UserRole.ADMIN; break;
            default: newRole = null; break;
        }
        if (newRole == null) { System.out.println("  Invalid role."); return; }
        boolean ok = userService.changeRole(username, newRole);
        System.out.println(ok ? "  Role updated." : "  User not found.");
        ConsoleUtils.pressEnter();
    }

    private void viewAllExpenses() {
        ConsoleUtils.printHeader("ALL EXPENSES (ADMIN VIEW)");
        List<Expense> list = financeService.getAllExpenses();
        if (list.isEmpty()) { System.out.println("  None."); }
        else { for (Expense e : list) System.out.println("  [" + e.getOwner() + "] " + e); }
        ConsoleUtils.pressEnter();
    }

    private void viewAllIncome() {
        ConsoleUtils.printHeader("ALL INCOME (ADMIN VIEW)");
        List<Income> list = financeService.getAllIncome();
        if (list.isEmpty()) { System.out.println("  None."); }
        else { for (Income i : list) System.out.println("  [" + i.getOwner() + "] " + i); }
        ConsoleUtils.pressEnter();
    }

    private void viewAllSubs() {
        ConsoleUtils.printHeader("ALL SUBSCRIPTIONS (ADMIN VIEW)");
        List<Subscription> list = financeService.getAllSubscriptions();
        if (list.isEmpty()) { System.out.println("  None."); }
        else { for (Subscription s : list) System.out.println("  [" + s.getOwner() + "] " + s); }
        ConsoleUtils.pressEnter();
    }
}
