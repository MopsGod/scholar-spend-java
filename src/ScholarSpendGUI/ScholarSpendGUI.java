package ScholarSpendGUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ScholarSpendGUI {

    private JFrame frame;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    // Table Models (Background Data)
    private DefaultTableModel expTableModel;
    private DefaultTableModel incTableModel;
    private DefaultTableModel subTableModel;
    private DefaultTableModel savTableModel;
    private DefaultTableModel budTableModel; 

    // Display Models (UI Only)
    private DefaultTableModel budDisplayModel; 

    // Analytics Labels
    private JLabel lblBalance;
    private JLabel lblFts;
    private JLabel lblFatigue;
    private JLabel lblSurvival;

    // App State
    private String loggedInUser = "";
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ScholarSpendGUI().createAndShowGUI());
    }

    public void createAndShowGUI() {
        setupDarkTheme();
        initTableModels(); 

        frame = new JFrame("ScholarSpend - Finance Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 600);
        frame.setLocationRelativeTo(null); 

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(createLoginPanel(), "LOGIN");
        cardPanel.add(createRegisterPanel(), "REGISTER"); 
        cardPanel.add(createMainMenuPanel(), "MAIN_MENU");
        cardPanel.add(createTransactionsPanel(), "TRANSACTIONS");
        cardPanel.add(createBudgetsPanel(), "BUDGETS");
        cardPanel.add(createSubscriptionsPanel(), "SUBSCRIPTIONS");
        cardPanel.add(createSavingsPanel(), "SAVINGS");
        cardPanel.add(createAnalyticsPanel(), "ANALYTICS");

        frame.add(cardPanel);
        frame.setVisible(true);
        
        cardLayout.show(cardPanel, "LOGIN");
    }

    // --- DATA PERSISTENCE & VALIDATION HELPERS ---

    private boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, dateFormatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private int getNextId(DefaultTableModel model) {
        int maxId = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                int id = Integer.parseInt(model.getValueAt(i, 0).toString());
                if (id > maxId) maxId = id;
            } catch (NumberFormatException ignored) {}
        }
        return maxId + 1;
    }

    private void saveAllUserData() {
        if (loggedInUser.isEmpty()) return;
        try {
            File dataDir = new File("data");
            if (!dataDir.exists()) dataDir.mkdir();

            exportTableToCSV(expTableModel, new File(dataDir, loggedInUser + "_expenses.csv"));
            exportTableToCSV(incTableModel, new File(dataDir, loggedInUser + "_income.csv"));
            exportTableToCSV(subTableModel, new File(dataDir, loggedInUser + "_subscriptions.csv"));
            exportTableToCSV(savTableModel, new File(dataDir, loggedInUser + "_savings.csv"));
            exportTableToCSV(budTableModel, new File(dataDir, loggedInUser + "_budgets.csv")); 
        } catch (Exception e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    private void loadUserData(String user) {
        File dataDir = new File("data");
        loadTableFromCSV(expTableModel, new File(dataDir, user + "_expenses.csv"));
        loadTableFromCSV(incTableModel, new File(dataDir, user + "_income.csv"));
        loadTableFromCSV(subTableModel, new File(dataDir, user + "_subscriptions.csv"));
        loadTableFromCSV(savTableModel, new File(dataDir, user + "_savings.csv"));
        loadTableFromCSV(budTableModel, new File(dataDir, user + "_budgets.csv")); 
    }

    private void loadTableFromCSV(DefaultTableModel model, File file) {
        model.setRowCount(0); 
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); 
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    model.addRow(line.split(",")); 
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading file " + file.getName() + ": " + e.getMessage());
        }
    }

    private void exportTableToCSV(DefaultTableModel model, File file) throws Exception {
        try (PrintWriter pw = new PrintWriter(file)) {
            for (int c = 0; c < model.getColumnCount(); c++) {
                pw.print(model.getColumnName(c));
                if (c < model.getColumnCount() - 1) pw.print(",");
            }
            pw.println();
            for (int r = 0; r < model.getRowCount(); r++) {
                for (int c = 0; c < model.getColumnCount(); c++) {
                    pw.print(model.getValueAt(r, c));
                    if (c < model.getColumnCount() - 1) pw.print(",");
                }
                pw.println();
            }
        }
    }

    // --- INITIALIZE DATA MODELS ---
    private void initTableModels() {
        expTableModel = new DefaultTableModel(new String[]{"ID", "User", "Description", "Amount", "Category", "Date"}, 0);
        incTableModel = new DefaultTableModel(new String[]{"ID", "User", "Source", "Amount", "Date"}, 0);
        subTableModel = new DefaultTableModel(new String[]{"ID", "User", "Name", "Monthly Cost", "Status", "Next Billing"}, 0);
        savTableModel = new DefaultTableModel(new String[]{"ID", "User", "Goal Name", "Target Amount", "Current Saved", "Progress %"}, 0);
        
        budTableModel = new DefaultTableModel(new String[]{"ID", "User", "Category", "Limit"}, 0);
        budDisplayModel = new DefaultTableModel(new String[]{"Category", "Monthly Limit", "Spent This Month", "Remaining"}, 0);
    }

    // --- SCREEN: LOGIN ---
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout()); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Welcome to ScholarSpend");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JTextField userField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Create New Account");

        loginButton.addActionListener(e -> {
            if (!userField.getText().trim().isEmpty()) {
                loggedInUser = userField.getText().trim(); 
                loadUserData(loggedInUser); 
                cardLayout.show(cardPanel, "MAIN_MENU");
            } else {
                JOptionPane.showMessageDialog(frame, "Please enter a valid username.", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerButton.addActionListener(e -> cardLayout.show(cardPanel, "REGISTER"));

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; panel.add(titleLabel, gbc);
        gbc.gridy = 1; gbc.gridwidth = 1; panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; panel.add(userField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; panel.add(passField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; panel.add(loginButton, gbc);
        gbc.gridy = 4; panel.add(registerButton, gbc);

        return panel;
    }

    // --- SCREEN: REGISTER ---
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout()); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Register New Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JTextField userField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);
        JPasswordField confirmPassField = new JPasswordField(15);
        JButton registerButton = new JButton("Register & Login");
        JButton backButton = new JButton("Back to Login");

        registerButton.addActionListener(e -> {
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword());
            String confirm = new String(confirmPassField.getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!pass.equals(confirm)) {
                JOptionPane.showMessageDialog(frame, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                loggedInUser = user;
                loadUserData(loggedInUser); 
                JOptionPane.showMessageDialog(frame, "Account created successfully for " + user + "!");
                userField.setText(""); passField.setText(""); confirmPassField.setText("");
                cardLayout.show(cardPanel, "MAIN_MENU");
            }
        });

        backButton.addActionListener(e -> cardLayout.show(cardPanel, "LOGIN"));

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; panel.add(titleLabel, gbc);
        gbc.gridy = 1; gbc.gridwidth = 1; panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; panel.add(userField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; panel.add(passField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1; panel.add(confirmPassField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; panel.add(registerButton, gbc);
        gbc.gridy = 5; panel.add(backButton, gbc);

        return panel;
    }

    // --- SCREEN: MAIN MENU ---
    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 20, 8, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JLabel titleLabel = new JLabel("Main Menu");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JButton btnAddIncome = new JButton("1. Add Income");
        JButton btnAddExpense = new JButton("2. Add Expense");
        JButton btnTransactions = new JButton("3. View Transactions");
        JButton btnBudgets = new JButton("4. Budgets");
        JButton btnSubscriptions = new JButton("5. Subscriptions");
        JButton btnSavings = new JButton("6. Savings Goals");
        JButton btnAnalytics = new JButton("7. Analytics & Metrics");
        JButton btnLogout = new JButton("0. Logout");

        btnAddIncome.addActionListener(e -> {
            JTextField sourceField = new JTextField();
            JTextField amountField = new JTextField();
            JTextField dateField = new JTextField(LocalDate.now().format(dateFormatter)); 

            Object[] message = {"Source (e.g., Job, Family):", sourceField, "Amount (€):", amountField, "Date (dd/MM/yyyy):", dateField};
            if (JOptionPane.showConfirmDialog(frame, message, "Add Income", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    double amount = Double.parseDouble(amountField.getText().trim().replace(",", "."));
                    if (amount < 0) throw new NumberFormatException();
                    
                    if (!isValidDate(dateField.getText().trim())) {
                        JOptionPane.showMessageDialog(frame, "Invalid date format! Please use dd/MM/yyyy.", "Date Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String safeSource = sourceField.getText().replace(",", " ");
                    String formattedAmt = String.format(Locale.US, "%.2f", amount);

                    incTableModel.addRow(new Object[]{String.valueOf(getNextId(incTableModel)), loggedInUser, safeSource, formattedAmt, dateField.getText().trim()});
                    saveAllUserData(); 
                    JOptionPane.showMessageDialog(frame, "Income Added Successfully!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid positive number for amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnAddExpense.addActionListener(e -> {
            JTextField descField = new JTextField();
            JTextField amountField = new JTextField();
            JTextField dateField = new JTextField(LocalDate.now().format(dateFormatter));
            JComboBox<String> categoryBox = new JComboBox<>(new String[]{"FOOD", "HOUSING", "TRANSPORT", "ENTERTAINMENT", "HEALTH", "OTHER"});
            
            Object[] message = {"Description:", descField, "Amount (€):", amountField, "Date (dd/MM/yyyy):", dateField, "Category:", categoryBox};
            if (JOptionPane.showConfirmDialog(frame, message, "Add Expense", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    double amount = Double.parseDouble(amountField.getText().trim().replace(",", "."));
                    if (amount < 0) throw new NumberFormatException();
                    
                    if (!isValidDate(dateField.getText().trim())) {
                        JOptionPane.showMessageDialog(frame, "Invalid date format! Please use dd/MM/yyyy.", "Date Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String safeDesc = descField.getText().replace(",", " ");
                    String formattedAmt = String.format(Locale.US, "%.2f", amount);

                    expTableModel.addRow(new Object[]{String.valueOf(getNextId(expTableModel)), loggedInUser, safeDesc, formattedAmt, categoryBox.getSelectedItem(), dateField.getText().trim()});
                    saveAllUserData(); 
                    JOptionPane.showMessageDialog(frame, "Expense Added Successfully!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid positive number for amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnTransactions.addActionListener(e -> cardLayout.show(cardPanel, "TRANSACTIONS"));
        
        btnBudgets.addActionListener(e -> {
            updateBudgetViewData();
            cardLayout.show(cardPanel, "BUDGETS");
        });
        
        btnSubscriptions.addActionListener(e -> cardLayout.show(cardPanel, "SUBSCRIPTIONS"));
        btnSavings.addActionListener(e -> cardLayout.show(cardPanel, "SAVINGS"));
        
        btnAnalytics.addActionListener(e -> {
            updateAnalyticsData();
            cardLayout.show(cardPanel, "ANALYTICS");
        });
        
        btnLogout.addActionListener(e -> {
            saveAllUserData(); 
            loggedInUser = ""; 
            cardLayout.show(cardPanel, "LOGIN");
        });

        int y = 0;
        gbc.gridy = y++; panel.add(titleLabel, gbc);
        gbc.gridy = y++; panel.add(btnAddIncome, gbc);
        gbc.gridy = y++; panel.add(btnAddExpense, gbc);
        gbc.gridy = y++; panel.add(btnTransactions, gbc);
        gbc.gridy = y++; panel.add(btnBudgets, gbc);
        gbc.gridy = y++; panel.add(btnSubscriptions, gbc);
        gbc.gridy = y++; panel.add(btnSavings, gbc);
        gbc.gridy = y++; panel.add(btnAnalytics, gbc);
        gbc.insets = new Insets(20, 20, 10, 20);
        gbc.gridy = y++; panel.add(btnLogout, gbc);

        return panel;
    }

    // --- SCREEN: TRANSACTIONS ---
    private JPanel createTransactionsPanel() {
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel expPanel = new JPanel(new BorderLayout());
        JTable expTable = new JTable(expTableModel);
        JButton btnRemoveExp = new JButton("Remove Selected Expense");
        btnRemoveExp.addActionListener(e -> {
            int row = expTable.getSelectedRow();
            if (row >= 0) {
                expTableModel.removeRow(row);
                saveAllUserData(); 
            } else JOptionPane.showMessageDialog(frame, "Please select an expense to remove.");
        });
        expPanel.add(new JScrollPane(expTable), BorderLayout.CENTER);
        expPanel.add(btnRemoveExp, BorderLayout.SOUTH);

        JPanel incPanel = new JPanel(new BorderLayout());
        JTable incTable = new JTable(incTableModel);
        JButton btnRemoveInc = new JButton("Remove Selected Income");
        btnRemoveInc.addActionListener(e -> {
            int row = incTable.getSelectedRow();
            if (row >= 0) {
                incTableModel.removeRow(row);
                saveAllUserData(); 
            } else JOptionPane.showMessageDialog(frame, "Please select an income to remove.");
        });
        incPanel.add(new JScrollPane(incTable), BorderLayout.CENTER);
        incPanel.add(btnRemoveInc, BorderLayout.SOUTH);

        tabbedPane.addTab("Expenses", expPanel);
        tabbedPane.addTab("Income", incPanel);

        return createScreenTemplate("Transactions", tabbedPane);
    }

    // --- SCREEN: BUDGETS (COMPLETELY REBUILT) ---
    private JPanel createBudgetsPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        JTable table = new JTable(budDisplayModel); 
        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnSetBudget = new JButton("Set Category Budget");

        btnSetBudget.addActionListener(e -> {
            JComboBox<String> categoryBox = new JComboBox<>(new String[]{"FOOD", "HOUSING", "TRANSPORT", "ENTERTAINMENT", "HEALTH", "OTHER"});
            JTextField limitField = new JTextField();
            Object[] message = {"Category:", categoryBox, "Monthly Limit (€):", limitField};
            
            if (JOptionPane.showConfirmDialog(frame, message, "Set Budget Limit", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    double limit = Double.parseDouble(limitField.getText().trim().replace(",", "."));
                    if (limit < 0) throw new NumberFormatException();
                    
                    String selectedCat = categoryBox.getSelectedItem().toString();
                    String formattedLimit = String.format(Locale.US, "%.2f", limit);

                    boolean found = false;
                    for(int i = 0; i < budTableModel.getRowCount(); i++) {
                        if(budTableModel.getValueAt(i, 2).toString().equals(selectedCat)) {
                            budTableModel.setValueAt(formattedLimit, i, 3);
                            found = true;
                            break;
                        }
                    }
                    if(!found) {
                        budTableModel.addRow(new Object[]{String.valueOf(getNextId(budTableModel)), loggedInUser, selectedCat, formattedLimit});
                    }
                    
                    saveAllUserData(); 
                    updateBudgetViewData(); 
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid positive number for limit.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnPanel.add(btnSetBudget);
        centerPanel.add(btnPanel, BorderLayout.SOUTH);

        return createScreenTemplate("Monthly Budgets", centerPanel);
    }

    // --- BUDGET LOGIC/MATH ---
    private void updateBudgetViewData() {
        budDisplayModel.setRowCount(0); 
        
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        Map<String, Double> spentThisMonth = new HashMap<>();
        for (int i = 0; i < expTableModel.getRowCount(); i++) {
            try {
                LocalDate expDate = LocalDate.parse(expTableModel.getValueAt(i, 5).toString(), dateFormatter);
                if (expDate.getMonthValue() == currentMonth && expDate.getYear() == currentYear) {
                    String category = expTableModel.getValueAt(i, 4).toString();
                    double amount = Double.parseDouble(expTableModel.getValueAt(i, 3).toString());
                    spentThisMonth.put(category, spentThisMonth.getOrDefault(category, 0.0) + amount);
                }
            } catch (Exception ignored) {} 
        }

        for (int i = 0; i < budTableModel.getRowCount(); i++) {
            String category = budTableModel.getValueAt(i, 2).toString();
            double limit = Double.parseDouble(budTableModel.getValueAt(i, 3).toString());
            
            double spent = spentThisMonth.getOrDefault(category, 0.0);
            double remaining = limit - spent;
            
            budDisplayModel.addRow(new Object[]{
                category, 
                String.format(Locale.US, "€%.2f", limit),
                String.format(Locale.US, "€%.2f", spent),
                String.format(Locale.US, "€%.2f", remaining)
            });
        }
    }

    // --- SCREEN: SUBSCRIPTIONS ---
    private JPanel createSubscriptionsPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        JTable table = new JTable(subTableModel);
        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Add Subscription");
        JButton btnRem = new JButton("Remove Selected");

        btnAdd.addActionListener(e -> {
            JTextField nameField = new JTextField();
            JTextField costField = new JTextField();
            JTextField dateField = new JTextField(LocalDate.now().format(dateFormatter)); 
            
            Object[] message = {"Name:", nameField, "Monthly Cost (€):", costField, "Next Billing Date (dd/MM/yyyy):", dateField};
            if (JOptionPane.showConfirmDialog(frame, message, "Add Subscription", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    double amount = Double.parseDouble(costField.getText().trim().replace(",", "."));
                    
                    if (!isValidDate(dateField.getText().trim())) {
                        JOptionPane.showMessageDialog(frame, "Invalid date format! Please use dd/MM/yyyy.", "Date Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String safeName = nameField.getText().replace(",", " ");
                    String formattedAmt = String.format(Locale.US, "%.2f", amount);

                    subTableModel.addRow(new Object[]{String.valueOf(getNextId(subTableModel)), loggedInUser, safeName, formattedAmt, "ACTIVE", dateField.getText().trim()});
                    saveAllUserData(); 
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid number for monthly cost.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnRem.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                subTableModel.removeRow(row);
                saveAllUserData(); 
            } else JOptionPane.showMessageDialog(frame, "Please select a subscription to remove.");
        });

        btnPanel.add(btnAdd);
        btnPanel.add(btnRem);
        centerPanel.add(btnPanel, BorderLayout.SOUTH);

        return createScreenTemplate("Active Subscriptions", centerPanel);
    }

    // --- SCREEN: SAVINGS GOALS ---
    private JPanel createSavingsPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        JTable table = new JTable(savTableModel);
        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Add Goal");
        JButton btnRem = new JButton("Remove Selected");

        btnAdd.addActionListener(e -> {
            JTextField nameField = new JTextField();
            JTextField targetField = new JTextField();
            Object[] message = {"Goal Name:", nameField, "Target Amount (€):", targetField};
            if (JOptionPane.showConfirmDialog(frame, message, "Add Savings Goal", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    double amount = Double.parseDouble(targetField.getText().trim().replace(",", "."));
                    
                    String safeName = nameField.getText().replace(",", " ");
                    String formattedAmt = String.format(Locale.US, "%.2f", amount);

                    savTableModel.addRow(new Object[]{String.valueOf(getNextId(savTableModel)), loggedInUser, safeName, formattedAmt, "0.00", "0%"});
                    saveAllUserData(); 
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid number for target amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnRem.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                savTableModel.removeRow(row);
                saveAllUserData(); 
            } else JOptionPane.showMessageDialog(frame, "Please select a goal to remove.");
        });

        btnPanel.add(btnAdd);
        btnPanel.add(btnRem);
        centerPanel.add(btnPanel, BorderLayout.SOUTH);

        return createScreenTemplate("Savings Goals", centerPanel);
    }

    // --- SCREEN: ANALYTICS ---
    private JPanel createAnalyticsPanel() {
        JPanel pnl = new JPanel(new GridLayout(4, 1, 10, 10));
        pnl.setBackground(new Color(60, 63, 65));
        pnl.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        lblBalance = createMetricCard("Current Balance", "€0.00"); 
        lblFts = createMetricCard("Fun-to-Study Index", "0.00");
        lblFatigue = createMetricCard("Subscription Fatigue", "0.0%");
        lblSurvival = createMetricCard("Survival Metric", "0.00 Months");
        
        pnl.add(lblBalance);
        pnl.add(lblFts);
        pnl.add(lblFatigue);
        pnl.add(lblSurvival);
        
        return createScreenTemplate("Analytics & Financial Metrics", pnl);
    }

    // --- ANALYTICS CALCULATION LOGIC ---
    private void updateAnalyticsData() {
        double totalIncome = 0;
        double totalExpense = 0;
        double essentialExpense = 0;
        double leisureExpense = 0;
        double academicExpense = 0;
        double totalSubscriptions = 0;

        for (int i = 0; i < incTableModel.getRowCount(); i++) {
            totalIncome += Double.parseDouble(incTableModel.getValueAt(i, 3).toString());
        }

        for (int i = 0; i < expTableModel.getRowCount(); i++) {
            double amt = Double.parseDouble(expTableModel.getValueAt(i, 3).toString());
            String cat = expTableModel.getValueAt(i, 4).toString();
            totalExpense += amt;
            
            if (cat.equals("FOOD") || cat.equals("HOUSING") || cat.equals("TRANSPORT") || cat.equals("HEALTH")) {
                essentialExpense += amt;
            } else if (cat.equals("ENTERTAINMENT")) {
                leisureExpense += amt;
            } else if (cat.equals("OTHER")) {
                academicExpense += amt; 
            }
        }

        for (int i = 0; i < subTableModel.getRowCount(); i++) {
            totalSubscriptions += Double.parseDouble(subTableModel.getValueAt(i, 3).toString());
        }

        double balance = totalIncome - totalExpense;
        double survival = essentialExpense > 0 ? (balance / essentialExpense) : 0;
        double fts = academicExpense > 0 ? (leisureExpense / academicExpense) : (leisureExpense > 0 ? -1 : 0);
        double fatigue = totalIncome > 0 ? (totalSubscriptions / totalIncome) * 100 : 0;

        lblBalance.setText(String.format(Locale.US, "Current Balance: €%.2f", balance));
        
        if (fts == -1) {
            lblFts.setText("Fun-to-Study Index: Max (No Study Costs)");
        } else {
            lblFts.setText(String.format(Locale.US, "Fun-to-Study Index: %.2f", fts));
        }
        
        lblFatigue.setText(String.format(Locale.US, "Subscription Fatigue: %.1f%%", fatigue));
        
        if (essentialExpense == 0) {
            lblSurvival.setText("Survival Metric: No Essential Costs");
        } else {
            lblSurvival.setText(String.format(Locale.US, "Survival Metric: %.2f Months", survival));
        }
        
        if (balance < 0) {
            lblBalance.setForeground(new Color(255, 100, 100)); 
        } else {
            lblBalance.setForeground(Color.WHITE);
        }
    }

    // --- HELPER UI METHODS ---
    private JPanel createScreenTemplate(String title, JComponent centerContent) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(centerContent, BorderLayout.CENTER);

        JButton backButton = new JButton("<- Back to Menu");
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "MAIN_MENU"));
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(backButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JLabel createMetricCard(String title, String value) {
        JLabel label = new JLabel(title + ": " + value);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createLineBorder(new Color(85, 85, 85), 2));
        return label;
    }

    // --- THEME SETUP ---
    private void setupDarkTheme() {
        Color darkBackground = new Color(43, 43, 43);
        Color lightText = new Color(169, 183, 198);
        Color componentBackground = new Color(60, 63, 65);
        Color buttonColor = new Color(75, 110, 175);
        Color tableSelection = new Color(47, 101, 202);

        UIManager.put("Panel.background", darkBackground);
        UIManager.put("Label.foreground", lightText);
        UIManager.put("TabbedPane.background", componentBackground);
        UIManager.put("TabbedPane.foreground", Color.WHITE);
        UIManager.put("Table.background", componentBackground);
        UIManager.put("Table.foreground", Color.WHITE);
        UIManager.put("Table.gridColor", new Color(85, 85, 85));
        UIManager.put("Table.selectionBackground", tableSelection);
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("TableHeader.background", darkBackground);
        UIManager.put("TableHeader.foreground", lightText);
        UIManager.put("ScrollPane.background", darkBackground);
        UIManager.put("ScrollPane.getViewport().background", darkBackground);
        UIManager.put("TextField.background", componentBackground);
        UIManager.put("TextField.foreground", Color.WHITE);
        UIManager.put("PasswordField.background", componentBackground);
        UIManager.put("PasswordField.foreground", Color.WHITE);
        UIManager.put("ComboBox.background", componentBackground);
        UIManager.put("ComboBox.foreground", Color.WHITE);
        UIManager.put("Button.background", buttonColor);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("OptionPane.background", darkBackground);
        UIManager.put("OptionPane.messageForeground", lightText);
    }
}