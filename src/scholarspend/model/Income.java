package scholarspend.model;

import scholarspend.util.DateUtils;

import java.time.LocalDate;

public class Income {
    private int id;
    private String owner;
    private String source;
    private double amount;
    private LocalDate date;

    public Income(int id, String owner, String source, double amount, LocalDate date) {
        this.id = id;
        this.owner = owner;
        this.source = source;
        this.amount = amount;
        this.date = date;
    }

    public int getId() { return id; }
    public String getOwner() { return owner; }
    public String getSource() { return source; }
    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }

    public void setSource(String s) { this.source = s; }
    public void setAmount(double a) { this.amount = a; }
    public void setDate(LocalDate d) { this.date = d; }

    public String toCsv() {
        return id + "," + owner + "," + source.replace(",", ";") + ","
                + amount + "," + date.format(DateUtils.CSV);
    }

    public static Income fromCsv(String line) {
        String[] p = line.split(",", -1);
        if (p.length < 5) return null;
        try {
            return new Income(
                Integer.parseInt(p[0].trim()),
                p[1].trim(), p[2].trim(),
                Double.parseDouble(p[3].trim()),
                LocalDate.parse(p[4].trim(), DateUtils.CSV)
            );
        } catch (Exception e) { return null; }
    }

    @Override
    public String toString() {
        return String.format("[%d] %-25s  EUR%7.2f  %s", id, source, amount, DateUtils.format(date));
    }
}
