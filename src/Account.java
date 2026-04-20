package src;

import net.miginfocom.swing.MigLayout;
import org.apache.commons.validator.routines.EmailValidator;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

// Account class to handle account-related operations
// This will show account details and settings for logged-in customer

public class Account extends JFrame
{
    // Database connection properties
    private Connection connection;
    private PreparedStatement pstat;
    private ResultSet resultSet;
    Properties props = new Properties();
    String DB_URL = props.getDbUrl();
    String DB_USER = props.getDbUser();
    String DB_PASSWORD = props.getDbPassword();

    // Create account fields
    private JTextField nameField;
    private JTextField surnameField;
    private JTextField registerEmailField;
    private JTextField addressField;
    private JPasswordField registerPasswordField;
    private JPasswordField confirmPasswordField;

    private int customerID = -1;

    // Buttons
    private JButton loginBtn;
    private JButton registerBtn;

    // Constructor
    public Account()
    {
        setTitle("EatEatEat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new MigLayout("insets 10"));
        add(createRegisterPanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }

    // Register Panel
    public JPanel createRegisterPanel()
    {
        JPanel panel = new JPanel(new MigLayout("insets 30 40 30 40, wrap 2", "[right, 120][grow, fill, 250]", "[]10[]"));

        panel.setPreferredSize(new Dimension(500, 800));

        JLabel title = new JLabel("Create an Account");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(title, "span 2, align center, wrap 15");

        // Login button to open the login page
        loginBtn = new JButton("Login");
        loginBtn.setPreferredSize(new Dimension(150, 30));
        loginBtn.addActionListener(e -> {
            new Login();
            dispose();
        });

        // Register button is not needed on the registration page, but we can keep it for consistency
        // The button does nothing as it isnt needed
        registerBtn = new JButton("Register");
        registerBtn.setPreferredSize(new Dimension(150, 30));

        panel.add(loginBtn);
        panel.add(registerBtn);

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

        JButton createAccountBtn = new JButton("Create Account");
        createAccountBtn.setPreferredSize(new Dimension(150, 30));
        createAccountBtn.addActionListener(e -> handleRegister());
        panel.add(createAccountBtn, "span 2, align center, gaptop 10");

        return panel;
    }

    // Handle registration logic
    private void handleRegister()
    {
        // Verifier class to validate email and password
        Verifier v = new Verifier();

        // Get user input
        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        String email = registerEmailField.getText().trim();
        String address = addressField.getText().trim();
        String password = new String(registerPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validation
        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() ||
                address.isEmpty() || password.isEmpty() || confirmPassword.isEmpty())
        {
            showError("Please fill in all fields.");
            return;
        }

        // Email validation
        if (!v.verifyEmailFormat(email))
        {
            showError("Please enter a valid email address.");
            return;
        }

        // Password validation
        if (!v.verifyPasswordLength(password))
        {
            showError("Password must be at least 8 characters.");
            return;
        }

        if (!v.verifyPasswordMatch(password, confirmPassword))
        {
            showError("Passwords do not match.");
            return;
        }

        try {
            // Establish connection to database
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Check if the customer already exists in the database
            String checkEmail = "SELECT * FROM customers WHERE email = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkEmail);
            checkStmt.setString(1, email);
            ResultSet checkResult = checkStmt.executeQuery();

            if (checkResult.next()) {
                showError("Customer already exists. Please use a different email address.");
                return;
            }

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
    }

    // Show error message
    private void showError(String message)
    {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Main method to create and display the account frame
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
}
