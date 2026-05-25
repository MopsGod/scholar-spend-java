package scholarspend.model;

public class User {
    private String username;
    private String passwordHash;
    private UserRole role;

    public User(String username, String passwordHash, UserRole role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // Getters
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public UserRole getRole() { return role; }

    // Setters
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRole(UserRole role) { this.role = role; }

    public String toCsv() {
        return username + "," + passwordHash + "," + role.name();
    }

    public static User fromCsv(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 3) return null;
        return new User(parts[0], parts[1], UserRole.valueOf(parts[2]));
    }

    @Override
    public String toString() {
        return "User{username='" + username + "', role=" + role + "}";
    }
}
