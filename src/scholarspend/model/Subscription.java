package scholarspend.model;

public class Subscription {
    private int id;
    private String owner;
    private String name;
    private double monthlyCost;
    private boolean active;

    public Subscription(int id, String owner, String name, double monthlyCost, boolean active) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.monthlyCost = monthlyCost;
        this.active = active;
    }

    public int getId() { return id; }
    public String getOwner() { return owner; }
    public String getName() { return name; }
    public double getMonthlyCost() { return monthlyCost; }
    public boolean isActive() { return active; }

    public void setName(String name) { this.name = name; }
    public void setMonthlyCost(double monthlyCost) { this.monthlyCost = monthlyCost; }
    public void setActive(boolean active) { this.active = active; }

    public String toCsv() {
        return id + "," + owner + "," + name.replace(",", ";") + ","
                + monthlyCost + "," + active;
    }

    public static Subscription fromCsv(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 5) return null;
        try {
            int id = Integer.parseInt(parts[0].trim());
            String owner = parts[1].trim();
            String name = parts[2].trim();
            double cost = Double.parseDouble(parts[3].trim());
            boolean active = Boolean.parseBoolean(parts[4].trim());
            return new Subscription(id, owner, name, cost, active);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        String status = active ? "ACTIVE" : "INACTIVE";
        return String.format("[%d] %-20s  EUR%6.2f/month  [%s]", id, name, monthlyCost, status);
    }
}
