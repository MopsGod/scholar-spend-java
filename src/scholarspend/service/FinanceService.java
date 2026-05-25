package scholarspend.service;

import scholarspend.model.*;
import scholarspend.util.IdGenerator;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class FinanceService {

    private final CsvRepository repo;
    private List<Expense>      expenses;
    private List<Income>       incomeList;
    private List<Subscription> subscriptions;
    private List<Budget>       budgets;
    private List<Goal>         goals;

    public FinanceService(CsvRepository repo) {
        this.repo          = repo;
        this.expenses      = repo.loadExpenses();
        this.incomeList    = repo.loadIncome();
        this.subscriptions = repo.loadSubscriptions();
        this.budgets       = repo.loadBudgets();
        this.goals         = repo.loadGoals();
    }

    // =========================================================================
    // EXPENSE CRUD
    // =========================================================================

    public void addExpense(String owner, String desc, double amount,
                           ExpenseCategory cat, LocalDate date) {
        int id = IdGenerator.nextId(expenses.stream().map(Expense::getId).collect(Collectors.toList()));
        expenses.add(new Expense(id, owner, desc, amount, cat, date));
        repo.saveExpenses(expenses);
    }

    public List<Expense> getExpenses(String owner) {
        return expenses.stream()
                .filter(e -> e.getOwner().equalsIgnoreCase(owner))
                .collect(Collectors.toList());
    }

    public List<Expense> getAllExpenses() { return expenses; }

    public boolean editExpense(String owner, int id, String desc, double amount,
                               ExpenseCategory cat, LocalDate date) {
        Optional<Expense> t = expenses.stream()
                .filter(e -> e.getId() == id && e.getOwner().equalsIgnoreCase(owner))
                .findFirst();
        if (!t.isPresent()) return false;
        t.get().setDescription(desc); t.get().setAmount(amount);
        t.get().setCategory(cat);     t.get().setDate(date);
        repo.saveExpenses(expenses);
        return true;
    }

    public boolean deleteExpense(String owner, int id) {
        boolean r = expenses.removeIf(e -> e.getId() == id && e.getOwner().equalsIgnoreCase(owner));
        if (r) repo.saveExpenses(expenses);
        return r;
    }

    // =========================================================================
    // INCOME CRUD
    // =========================================================================

    public void addIncome(String owner, String source, double amount, LocalDate date) {
        int id = IdGenerator.nextId(incomeList.stream().map(Income::getId).collect(Collectors.toList()));
        incomeList.add(new Income(id, owner, source, amount, date));
        repo.saveIncome(incomeList);
    }

    public List<Income> getIncome(String owner) {
        return incomeList.stream()
                .filter(i -> i.getOwner().equalsIgnoreCase(owner))
                .collect(Collectors.toList());
    }

    public List<Income> getAllIncome() { return incomeList; }

    public boolean editIncome(String owner, int id, String source, double amount, LocalDate date) {
        Optional<Income> t = incomeList.stream()
                .filter(i -> i.getId() == id && i.getOwner().equalsIgnoreCase(owner))
                .findFirst();
        if (!t.isPresent()) return false;
        t.get().setSource(source); t.get().setAmount(amount); t.get().setDate(date);
        repo.saveIncome(incomeList);
        return true;
    }

    public boolean deleteIncome(String owner, int id) {
        boolean r = incomeList.removeIf(i -> i.getId() == id && i.getOwner().equalsIgnoreCase(owner));
        if (r) repo.saveIncome(incomeList);
        return r;
    }

    // =========================================================================
    // SUBSCRIPTION CRUD
    // =========================================================================

    public void addSubscription(String owner, String name, double cost) {
        int id = IdGenerator.nextId(subscriptions.stream().map(Subscription::getId).collect(Collectors.toList()));
        subscriptions.add(new Subscription(id, owner, name, cost, true));
        repo.saveSubscriptions(subscriptions);
    }

    public List<Subscription> getSubscriptions(String owner) {
        return subscriptions.stream()
                .filter(s -> s.getOwner().equalsIgnoreCase(owner))
                .collect(Collectors.toList());
    }

    public List<Subscription> getAllSubscriptions() { return subscriptions; }

    public boolean editSubscription(String owner, int id, String name,
                                    double cost, boolean active) {
        Optional<Subscription> t = subscriptions.stream()
                .filter(s -> s.getId() == id && s.getOwner().equalsIgnoreCase(owner))
                .findFirst();
        if (!t.isPresent()) return false;
        t.get().setName(name); t.get().setMonthlyCost(cost); t.get().setActive(active);
        repo.saveSubscriptions(subscriptions);
        return true;
    }

    public boolean deleteSubscription(String owner, int id) {
        boolean r = subscriptions.removeIf(s -> s.getId() == id && s.getOwner().equalsIgnoreCase(owner));
        if (r) repo.saveSubscriptions(subscriptions);
        return r;
    }

    // =========================================================================
    // BUDGET CRUD
    // =========================================================================

    public void addBudget(String owner, ExpenseCategory cat, double limit, LocalDate month) {
        int id = IdGenerator.nextId(budgets.stream().map(Budget::getId).collect(Collectors.toList()));
        budgets.add(new Budget(id, owner, cat, limit, month));
        repo.saveBudgets(budgets);
    }

    public List<Budget> getBudgets(String owner) {
        return budgets.stream()
                .filter(b -> b.getOwner().equalsIgnoreCase(owner))
                .collect(Collectors.toList());
    }

    public boolean deleteBudget(String owner, int id) {
        boolean r = budgets.removeIf(b -> b.getId() == id && b.getOwner().equalsIgnoreCase(owner));
        if (r) repo.saveBudgets(budgets);
        return r;
    }

    /**
     * How much over the budget limit the user has spent for a given category in a given month.
     * Returns 0 if under budget. Negative means still within budget.
     */
    public double overspend(String owner, ExpenseCategory cat, LocalDate month) {
        // Find applicable budget
        Optional<Budget> budget = budgets.stream()
                .filter(b -> b.getOwner().equalsIgnoreCase(owner)
                          && b.getCategory() == cat
                          && YearMonth.from(b.getMonth()).equals(YearMonth.from(month)))
                .findFirst();
        if (!budget.isPresent()) return 0.0;

        double spent = expenses.stream()
                .filter(e -> e.getOwner().equalsIgnoreCase(owner)
                          && e.getCategory() == cat
                          && YearMonth.from(e.getDate()).equals(YearMonth.from(month)))
                .mapToDouble(Expense::getAmount).sum();

        return spent - budget.get().getMonthlyLimit(); // positive = overspend
    }

    // =========================================================================
    // GOAL CRUD
    // =========================================================================

    public void addGoal(String owner, String name, double target, LocalDate deadline) {
        int id = IdGenerator.nextId(goals.stream().map(Goal::getId).collect(Collectors.toList()));
        goals.add(new Goal(id, owner, name, target, 0.0, deadline));
        repo.saveGoals(goals);
    }

    public List<Goal> getGoals(String owner) {
        return goals.stream()
                .filter(g -> g.getOwner().equalsIgnoreCase(owner))
                .collect(Collectors.toList());
    }

    public boolean addToGoal(String owner, int id, double amount) {
        Optional<Goal> t = goals.stream()
                .filter(g -> g.getId() == id && g.getOwner().equalsIgnoreCase(owner))
                .findFirst();
        if (!t.isPresent()) return false;
        t.get().setCurrentAmount(t.get().getCurrentAmount() + amount);
        repo.saveGoals(goals);
        return true;
    }

    public boolean deleteGoal(String owner, int id) {
        boolean r = goals.removeIf(g -> g.getId() == id && g.getOwner().equalsIgnoreCase(owner));
        if (r) repo.saveGoals(goals);
        return r;
    }

    // =========================================================================
    // FINANCIAL METRICS
    // =========================================================================

    public double funToStudyIndex(String owner) {
        List<Expense> ex = getExpenses(owner);
        double leisure  = ex.stream().filter(e -> e.getCategory().isLeisure()).mapToDouble(Expense::getAmount).sum();
        double academic = ex.stream().filter(e -> e.getCategory().isAcademic()).mapToDouble(Expense::getAmount).sum();
        if (academic == 0) return leisure > 0 ? Double.MAX_VALUE : 0.0;
        return leisure / academic;
    }

    public double subscriptionFatigueIndex(String owner) {
        double subTotal = getSubscriptions(owner).stream()
                .filter(Subscription::isActive).mapToDouble(Subscription::getMonthlyCost).sum();
        double income   = getIncome(owner).stream().mapToDouble(Income::getAmount).sum();
        if (income == 0) return 0.0;
        return (subTotal / income) * 100.0;
    }

    public double survivalMetric(String owner) {
        double balance   = getBalance(owner);
        double essentials = getExpenses(owner).stream()
                .filter(e -> e.getCategory().isEssential()).mapToDouble(Expense::getAmount).sum();
        if (essentials == 0) return balance > 0 ? Double.MAX_VALUE : 0.0;
        return balance / essentials;
    }

    public double getBalance(String owner) {
        double income = getIncome(owner).stream().mapToDouble(Income::getAmount).sum();
        double spent  = getExpenses(owner).stream().mapToDouble(Expense::getAmount).sum();
        return income - spent;
    }

    // =========================================================================
    // MONTHLY SUMMARY  (sorted by month ascending)
    // =========================================================================

    /**
     * Returns a map of YearMonth -> net balance (income - expenses) for that month.
     * Sorted by month ascending.
     */
    public Map<YearMonth, Double> monthlyBalance(String owner) {
        Map<YearMonth, Double> result = new TreeMap<>();

        for (Income i : getIncome(owner)) {
            YearMonth ym = YearMonth.from(i.getDate());
            result.merge(ym, i.getAmount(), Double::sum);
        }
        for (Expense e : getExpenses(owner)) {
            YearMonth ym = YearMonth.from(e.getDate());
            result.merge(ym, -e.getAmount(), Double::sum);
        }
        return result;
    }

    /**
     * Returns total expenses per category for a given month.
     */
    public Map<ExpenseCategory, Double> expensesByCategory(String owner, YearMonth month) {
        Map<ExpenseCategory, Double> result = new LinkedHashMap<>();
        for (ExpenseCategory cat : ExpenseCategory.values()) result.put(cat, 0.0);
        for (Expense e : getExpenses(owner)) {
            if (YearMonth.from(e.getDate()).equals(month)) {
                result.merge(e.getCategory(), e.getAmount(), Double::sum);
            }
        }
        return result;
    }

    /**
     * Returns total expenses per category across ALL time (for the bar chart).
     */
    public Map<ExpenseCategory, Double> expensesByCategoryAllTime(String owner) {
        Map<ExpenseCategory, Double> result = new LinkedHashMap<>();
        for (ExpenseCategory cat : ExpenseCategory.values()) result.put(cat, 0.0);
        for (Expense e : getExpenses(owner)) {
            result.merge(e.getCategory(), e.getAmount(), Double::sum);
        }
        return result;
    }

    // =========================================================================
    // PREDICTION  –  simple linear regression on last N months' total expenses
    // =========================================================================

    /**
     * Predicts next month's total expenses using simple linear regression
     * over the last 'months' data points. Falls back to average if < 2 points.
     */
    public double predictNextMonth(String owner, int lookbackMonths) {
        Map<YearMonth, Double> monthly = new TreeMap<>();
        for (Expense e : getExpenses(owner)) {
            YearMonth ym = YearMonth.from(e.getDate());
            monthly.merge(ym, e.getAmount(), Double::sum);
        }

        List<Double> values = new ArrayList<>(monthly.values());
        int n = values.size();
        if (n == 0) return 0.0;

        // Take last N months
        int start = Math.max(0, n - lookbackMonths);
        List<Double> window = values.subList(start, n);
        int wn = window.size();

        if (wn < 2) {
            // Just return the average
            return window.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        }

        // Linear regression: y = a + b*x, x = 0,1,2,...
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < wn; i++) {
            sumX  += i;
            sumY  += window.get(i);
            sumXY += i * window.get(i);
            sumX2 += (double) i * i;
        }
        double denom = wn * sumX2 - sumX * sumX;
        if (denom == 0) return sumY / wn;

        double b = (wn * sumXY - sumX * sumY) / denom;
        double a = (sumY - b * sumX) / wn;

        return Math.max(0.0, a + b * wn); // next point after last
    }

    public void reload() {
        this.expenses      = repo.loadExpenses();
        this.incomeList    = repo.loadIncome();
        this.subscriptions = repo.loadSubscriptions();
        this.budgets       = repo.loadBudgets();
        this.goals         = repo.loadGoals();
    }
}
