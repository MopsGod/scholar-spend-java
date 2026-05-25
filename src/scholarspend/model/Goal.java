package scholarspend.model;

import scholarspend.util.DateUtils;

import java.time.LocalDate;

/**
 * A savings goal (e.g. "New GPU", target EUR 800).
 */
public class Goal {
    private int id;
    private String owner;
    private String name;
    private double targetAmount;
    private double currentAmount;
    private LocalDate deadline;

    public Goal(int id, String owner, String name,
                double targetAmount, double currentAmount, LocalDate deadline) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline = deadline;
    }

    public int getId() { return id; }
    public String getOwner() { return owner; }
    public String getName() { return name; }
    public double getTargetAmount() { return targetAmount; }
    public double getCurrentAmount() { return currentAmount; }
    public LocalDate getDeadline() { return deadline; }

    public void setName(String n) { this.name = n; }
    public void setTargetAmount(double t) { this.targetAmount = t; }
    public void setCurrentAmount(double c) { this.currentAmount = c; }
    public void setDeadline(LocalDate d) { this.deadline = d; }

    /** 0.0 – 100.0 */
    public double progressPercent() {
        if (targetAmount <= 0) return 100.0;
        return Math.min(100.0, (currentAmount / targetAmount) * 100.0);
    }

    public boolean isComplete() { return currentAmount >= targetAmount; }

    public String toCsv() {
        return id + "," + owner + "," + name.replace(",", ";") + ","
                + targetAmount + "," + currentAmount + ","
                + deadline.format(DateUtils.CSV);
    }

    public static Goal fromCsv(String line) {
        String[] p = line.split(",", -1);
        if (p.length < 6) return null;
        try {
            return new Goal(
                Integer.parseInt(p[0].trim()),
                p[1].trim(), p[2].trim(),
                Double.parseDouble(p[3].trim()),
                Double.parseDouble(p[4].trim()),
                LocalDate.parse(p[5].trim(), DateUtils.CSV)
            );
        } catch (Exception e) { return null; }
    }

    @Override
    public String toString() {
        return String.format("[%d] %-20s  EUR%.2f / EUR%.2f (%.0f%%)  Deadline: %s",
                id, name, currentAmount, targetAmount,
                progressPercent(), DateUtils.format(deadline));
    }
}
