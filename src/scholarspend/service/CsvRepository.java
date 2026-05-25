package scholarspend.service;

import scholarspend.model.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CsvRepository {

    private static final String DATA_DIR          = "data";
    private static final String USERS_FILE        = DATA_DIR + "/users.csv";
    private static final String EXPENSES_FILE     = DATA_DIR + "/expenses.csv";
    private static final String INCOME_FILE       = DATA_DIR + "/income.csv";
    private static final String SUBS_FILE         = DATA_DIR + "/subscriptions.csv";
    private static final String BUDGETS_FILE      = DATA_DIR + "/budgets.csv";
    private static final String GOALS_FILE        = DATA_DIR + "/goals.csv";

    public CsvRepository() {
        try { Files.createDirectories(Paths.get(DATA_DIR)); }
        catch (IOException e) { System.err.println("Warning: could not create data directory."); }
    }

    // ── Users ────────────────────────────────────────────────────────────────
    public List<User> loadUsers() {
        List<User> list = new ArrayList<>();
        for (String l : readLines(USERS_FILE)) { User u = User.fromCsv(l); if (u != null) list.add(u); }
        return list;
    }
    public void saveUsers(List<User> list) { writeLines(USERS_FILE, toCsvLines(list)); }

    // ── Expenses ─────────────────────────────────────────────────────────────
    public List<Expense> loadExpenses() {
        List<Expense> list = new ArrayList<>();
        for (String l : readLines(EXPENSES_FILE)) { Expense e = Expense.fromCsv(l); if (e != null) list.add(e); }
        return list;
    }
    public void saveExpenses(List<Expense> list) { writeLines(EXPENSES_FILE, toCsvLines(list)); }

    // ── Income ───────────────────────────────────────────────────────────────
    public List<Income> loadIncome() {
        List<Income> list = new ArrayList<>();
        for (String l : readLines(INCOME_FILE)) { Income i = Income.fromCsv(l); if (i != null) list.add(i); }
        return list;
    }
    public void saveIncome(List<Income> list) { writeLines(INCOME_FILE, toCsvLines(list)); }

    // ── Subscriptions ────────────────────────────────────────────────────────
    public List<Subscription> loadSubscriptions() {
        List<Subscription> list = new ArrayList<>();
        for (String l : readLines(SUBS_FILE)) { Subscription s = Subscription.fromCsv(l); if (s != null) list.add(s); }
        return list;
    }
    public void saveSubscriptions(List<Subscription> list) { writeLines(SUBS_FILE, toCsvLines(list)); }

    // ── Budgets ──────────────────────────────────────────────────────────────
    public List<Budget> loadBudgets() {
        List<Budget> list = new ArrayList<>();
        for (String l : readLines(BUDGETS_FILE)) { Budget b = Budget.fromCsv(l); if (b != null) list.add(b); }
        return list;
    }
    public void saveBudgets(List<Budget> list) { writeLines(BUDGETS_FILE, toCsvLines(list)); }

    // ── Goals ────────────────────────────────────────────────────────────────
    public List<Goal> loadGoals() {
        List<Goal> list = new ArrayList<>();
        for (String l : readLines(GOALS_FILE)) { Goal g = Goal.fromCsv(l); if (g != null) list.add(g); }
        return list;
    }
    public void saveGoals(List<Goal> list) { writeLines(GOALS_FILE, toCsvLines(list)); }

    // ── Helpers ──────────────────────────────────────────────────────────────
    private interface CsvSerializable { String toCsv(); }

    private <T> List<String> toCsvLines(List<T> items) {
        List<String> lines = new ArrayList<>();
        for (T item : items) {
            if (item instanceof User)         lines.add(((User) item).toCsv());
            else if (item instanceof Expense) lines.add(((Expense) item).toCsv());
            else if (item instanceof Income)  lines.add(((Income) item).toCsv());
            else if (item instanceof Subscription) lines.add(((Subscription) item).toCsv());
            else if (item instanceof Budget)  lines.add(((Budget) item).toCsv());
            else if (item instanceof Goal)    lines.add(((Goal) item).toCsv());
        }
        return lines;
    }

    private List<String> readLines(String path) {
        List<String> lines = new ArrayList<>();
        File f = new File(path);
        if (!f.exists()) return lines;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) lines.add(line);
            }
        } catch (IOException e) { System.err.println("Error reading " + path + ": " + e.getMessage()); }
        return lines;
    }

    private void writeLines(String path, List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, false))) {
            for (String line : lines) { bw.write(line); bw.newLine(); }
        } catch (IOException e) { System.err.println("Error writing " + path + ": " + e.getMessage()); }
    }
}
