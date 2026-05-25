package scholarspend.model;

import scholarspend.util.DateUtils;

import java.time.LocalDate;

/**
 * A monthly spending limit per category.
 * month is stored as the first day of that month (e.g. 2026-04-01 = April 2026).
 */
public class Budget {
    private int id;
    private String owner;
    private ExpenseCategory category;
    private double monthlyLimit;
    private LocalDate month;   // first day of the target month

    public Budget(int id, String owner, ExpenseCategory category,
                  double monthlyLimit, LocalDate month) {
        this.id = id;
        this.owner = owner;
        this.category = category;
        this.monthlyLimit = monthlyLimit;
        this.month = month;
    }

    public int getId() { return id; }
    public String getOwner() { return owner; }
    public ExpenseCategory getCategory() { return category; }
    public double getMonthlyLimit() { return monthlyLimit; }
    public LocalDate getMonth() { return month; }

    public void setMonthlyLimit(double l) { this.monthlyLimit = l; }
    public void setMonth(LocalDate m) { this.month = m; }

    public String toCsv() {
        return id + "," + owner + "," + category.name() + ","
                + monthlyLimit + "," + month.format(DateUtils.CSV);
    }

    public static Budget fromCsv(String line) {
        String[] p = line.split(",", -1);
        if (p.length < 5) return null;
        try {
            return new Budget(
                Integer.parseInt(p[0].trim()),
                p[1].trim(),
                ExpenseCategory.valueOf(p[2].trim()),
                Double.parseDouble(p[3].trim()),
                LocalDate.parse(p[4].trim(), DateUtils.CSV)
            );
        } catch (Exception e) { return null; }
    }

    @Override
    public String toString() {
        String monthStr = month.getMonth().toString().substring(0, 3)
                + " " + month.getYear();
        return String.format("[%d] %-16s  Limit: EUR%7.2f  Month: %s",
                id, category, monthlyLimit, monthStr);
    }
}
