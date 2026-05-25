package scholarspend.service;

import scholarspend.model.User;
import scholarspend.model.UserRole;
import scholarspend.util.PasswordUtils;

import java.util.List;
import java.util.Optional;

public class UserService {

    private final CsvRepository repo;
    private List<User> users;

    public UserService(CsvRepository repo) {
        this.repo = repo;
        this.users = repo.loadUsers();

        // Seed a default admin if no users exist
        if (users.isEmpty()) {
            users.add(new User("admin", PasswordUtils.hash("admin123"), UserRole.ADMIN));
            repo.saveUsers(users);
        }
    }

    /**
     * Attempt login. Returns the User if credentials match, empty otherwise.
     */
    public Optional<User> login(String username, String password) {
        return users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username)
                          && PasswordUtils.verify(password, u.getPasswordHash()))
                .findFirst();
    }

    /**
     * Register a new NORMAL_USER. Returns false if username already taken.
     */
    public boolean register(String username, String password) {
        boolean exists = users.stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
        if (exists) return false;
        users.add(new User(username, PasswordUtils.hash(password), UserRole.NORMAL_USER));
        repo.saveUsers(users);
        return true;
    }

    /** Admin: change role of a user */
    public boolean changeRole(String username, UserRole newRole) {
        Optional<User> target = users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();
        if (target.isEmpty()) return false;
        target.get().setRole(newRole);
        repo.saveUsers(users);
        return true;
    }

    /** Admin: list all users */
    public List<User> getAllUsers() {
        return users;
    }

    /** Admin: delete a user */
    public boolean deleteUser(String username) {
        boolean removed = users.removeIf(u -> u.getUsername().equalsIgnoreCase(username));
        if (removed) repo.saveUsers(users);
        return removed;
    }

    public void reload() {
        this.users = repo.loadUsers();
    }
}
