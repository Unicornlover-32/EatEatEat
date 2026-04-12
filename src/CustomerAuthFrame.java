package src;

import org.apache.commons.validator.routines.EmailValidator;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CustomerAuthFrame extends JFrame
{
    // Database connection properties
    Properties props = new Properties();
    String DB_URL = props.getDbUrl();
    String DB_USER = props.getDbUser();
    String DB_PASSWORD = props.getDbPassword();
    Connection connection = null;
    PreparedStatement pstat = null;
    ResultSet resultSet = null;

    // Login fields
    private JTextField loginEmailField;
    private JPasswordField loginPasswordField;

    // Create account fields
    private JTextField nameField;
    private JTextField surnameField;
    private JTextField registerEmailField;
    private JTextField addressField;
    private JPasswordField registerPasswordField;
    private JPasswordField confirmPasswordField;

    // Retrieve Customer ID
    private int customerID = -1;

    public CustomerAuthFrame()
    {
        setTitle("Customer Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Tabbed pane to switch between login and register
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Login", createLoginPanel());
        tabbedPane.addTab("Create Account", createRegisterPanel());

        add(tabbedPane);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ── LOGIN PANEL ──────────────────────────────────────────────
    private JPanel createLoginPanel()
    {
        JPanel panel = new JPanel(new MigLayout("insets 30 40 30 40, wrap 2", "[right, 100][grow, fill, 250]", "[]15[]20[]"));

        JLabel title = new JLabel("Sign In");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(title, "span 2, align center, wrap 20");

        panel.add(new JLabel("Email:"));
        loginEmailField = new JTextField();
        panel.add(loginEmailField);

        panel.add(new JLabel("Password:"));
        loginPasswordField = new JPasswordField();
        panel.add(loginPasswordField);

        JButton loginBtn = new JButton("Login");
        loginBtn.setPreferredSize(new Dimension(120, 30));
        loginBtn.addActionListener(e -> handleLogin());
        panel.add(loginBtn, "span 2, align center, wrap");

        return panel;
    }

    // ── REGISTER PANEL ───────────────────────────────────────────
    private JPanel createRegisterPanel()
    {
        JPanel panel = new JPanel(new MigLayout("insets 30 40 30 40, wrap 2", "[right, 120][grow, fill, 250]", "[]10[]"));

        JLabel title = new JLabel("Create an Account");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(title, "span 2, align center, wrap 15");

        panel.add(new JLabel("First Name:"));
        nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Surname:"));
        surnameField = new JTextField();
        panel.add(surnameField);

        panel.add(new JLabel("Email:"));
        registerEmailField = new JTextField();
        panel.add(registerEmailField);

        panel.add(new JLabel("Address:"));
        addressField = new JTextField();
        panel.add(addressField);

        panel.add(new JLabel("Password:"));
        registerPasswordField = new JPasswordField();
        panel.add(registerPasswordField);

        panel.add(new JLabel("Confirm Password:"));
        confirmPasswordField = new JPasswordField();
        panel.add(confirmPasswordField);

        JButton registerBtn = new JButton("Create Account");
        registerBtn.setPreferredSize(new Dimension(150, 30));
        registerBtn.addActionListener(e -> handleRegister());
        panel.add(registerBtn, "span 2, align center, gaptop 10");

        return panel;
    }

    // ── HANDLERS ─────────────────────────────────────────────────
    private void handleLogin()
    {
        String email = loginEmailField.getText().trim();
        String password = new String(loginPasswordField.getPassword());

        if (email.isEmpty() || password.isEmpty())
        {
            showError("Please fill in all fields.");
            return;
        }

        // Email validation
        if (!EmailValidator.getInstance().isValid(email))
        {
            showError("Please enter a valid email address.");
            return;
        }

        try
        {
            // Establish connection to database
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // SQL query to check if email and password match
            String retrieve = "SELECT customerID FROM customers WHERE email = ? AND password = ?";

            pstat = connection.prepareStatement(retrieve);
            // Create prepared statement for retrieving data from the table
            pstat.setString(1, email);
            pstat.setString(2, password);
            resultSet = pstat.executeQuery();

            if (resultSet.next())
            {
                customerID = resultSet.getInt("customerID");
                JOptionPane.showMessageDialog(this, "Login successful!\nWelcome, " + customerID, "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            else
            {
                showError("Invalid email or password. Please try again.");
            }
        }
        catch (SQLException e)
        {
            showError("Database error: " + e.getMessage());
        }
        finally
        {
            openHomeFrame();
        }
    }

    private void handleRegister()
    {
        String name             = nameField.getText().trim();
        String surname          = surnameField.getText().trim();
        String email            = registerEmailField.getText().trim();
        String address          = addressField.getText().trim();
        String password         = new String(registerPasswordField.getPassword());
        String confirmPassword  = new String(confirmPasswordField.getPassword());

        // Validation
        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() ||
            address.isEmpty() || password.isEmpty() || confirmPassword.isEmpty())
        {
            showError("Please fill in all fields.");
            return;
        }

        // Email validation
        if (!EmailValidator.getInstance().isValid(email))
        {
            showError("Please enter a valid email address.");
            return;
        }

        if (password.length() < 8)
        {
            showError("Password must be at least 8 characters.");
            return;
        }

        if (!password.equals(confirmPassword))
        {
            showError("Passwords do not match.");
            return;
        }

        try {
            // Establish connection to database
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // SQL query to insert new customer into database
            String insert = "INSERT INTO customers (FirstName, SecondName, Address, Email, Password) VALUES (?, ?, ?, ?, ?)";

            pstat = connection.prepareStatement(insert);
            // Create prepared statement for inserting data into the table
            pstat.setString(1, name);
            pstat.setString(2, surname);
            pstat.setString(3, address);
            pstat.setString(4, email);
            pstat.setString(5, password);
            pstat.executeUpdate();

            // Retrieve customerID after successful registration
            String retrieve = "SELECT customerID FROM customers WHERE email = ? AND password = ?";
            // Establish connection to database
            pstat = connection.prepareStatement(retrieve);
            // Create prepared statement for retrieving data from the table
            pstat.setString(1, email);
            pstat.setString(2, password);
            resultSet = pstat.executeQuery();

            // Retrieve customerID from result set
            if (resultSet.next())
            {
                customerID = resultSet.getInt("customerID");
                JOptionPane.showMessageDialog(this, "Login successful!\nWelcome, " + customerID, "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch (SQLException e)
        {
            showError("Database error: " + e.getMessage());
            return;
        }
        finally
        {
            openHomeFrame();
        }
        System.out.println("Registering: " + name + " " + surname + " | " + email);
        JOptionPane.showMessageDialog(this, "Account created successfully!\nWelcome, " + name + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message)
    {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void openHomeFrame()
    {
        try
        {
            resultSet.close();
            pstat.close();
            connection.close();

            if(customerID != -1)
            {
                new HomeFrame(customerID);
                dispose();
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(CustomerAuthFrame::new);
    }
}