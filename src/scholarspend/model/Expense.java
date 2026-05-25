package scholarspend.model;

import scholarspend.util.DateUtils;

import java.time.LocalDate;

public class Expense {
    private int id;
    private String owner;
    private String description;
    private double amount;
    private ExpenseCategory category;
    private LocalDate date;

    public Expense(int id, String owner, String description, double amount,
                   ExpenseCategory category, LocalDate date) {
        this.id = id;
        this.owner = owner;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public int getId() { return id; }
    public String getOwner() { return owner; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public ExpenseCategory getCategory() { return category; }
    public LocalDate getDate() { return date; }

    public void setDescription(String d) { this.description = d; }
    public void setAmount(double a) { this.amount = a; }
    public void setCategory(ExpenseCategory c) { this.category = c; }
    public void setDate(LocalDate d) { this.date = d; }

    /** CSV uses ISO format (yyyy-MM-dd) for reliable parsing. */
    public String toCsv() {
        return id + "," + owner + "," + description.replace(",", ";") + ","
                + amount + "," + category.name() + "," + date.format(DateUtils.CSV);
    }

    public static Expense fromCsv(String line) {
        String[] p = line.split(",", -1);
        if (p.length < 6) return null;
        try {
            return new Expense(
                Integer.parseInt(p[0].trim()),
                p[1].trim(), p[2].trim(),
                Double.parseDouble(p[3].trim()),
                ExpenseCategory.valueOf(p[4].trim()),
                LocalDate.parse(p[5].trim(), DateUtils.CSV)
            );
        } catch (Exception e) { return null; }
    }

    @Override
    public String toString() {
        return String.format("[%d] %-25s  EUR%7.2f  %-16s  %s",
                id, description, amount, category, DateUtils.format(date));
    }
}
