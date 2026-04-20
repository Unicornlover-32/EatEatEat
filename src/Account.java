package src;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mindrot.jbcrypt.BCrypt;

// Account class to handle account-related operations
// This will show account details and settings for logged-in customer

public class Account extends JFrame
{
    private static final Log log = LogFactory.getLog(Account.class);
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

    // Verifier
    private Verifier v = new Verifier();

    // Constructor
    public Account()
    {
        setTitle("EatEatEat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(false);
        setLayout(new MigLayout("insets 10"));
        add(createRegisterPanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Register Panel
    public JPanel createRegisterPanel()
    {
        JPanel panel = new JPanel(new MigLayout("insets 30 40 30 40, wrap 1, fillx", "[grow, fill]"));
        panel.setPreferredSize(new Dimension(500, 800));

        JLabel title = new JLabel("Create an Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        panel.add(title, "align center, wrap 20");

        addFormField(panel, "First Name:", nameField = new JTextField());
        addFormField(panel, "Surname:", surnameField = new JTextField());
        addFormField(panel, "Email:", registerEmailField = new JTextField());
        addFormField(panel, "Address:", addressField = new JTextField());
        addFormField(panel, "Password:", registerPasswordField = new JPasswordField());
        addFormField(panel, "Confirm Password:", confirmPasswordField = new JPasswordField());

        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setPreferredSize(new Dimension(150, 30));
        loginBtn.addActionListener(e -> {
            new Login();
            dispose();
        });
        panel.add(loginBtn, "growx, wrap 20");

        JButton createAccountBtn = new JButton("Create Account");
        createAccountBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        createAccountBtn.setPreferredSize(new Dimension(150, 30));
        createAccountBtn.addActionListener(e -> handleRegister());
        panel.add(createAccountBtn, "growx");

        return panel;
    }

    private void addFormField(JPanel panel, String labelText, JTextField textField) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(label, "wrap 5");
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(textField, "growx, wrap 15");
    }

    // Handle registration logic
    private void handleRegister()
    {
        // Get user input
        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        String email = registerEmailField.getText().trim();
        String address = addressField.getText().trim();
        String password = new String(registerPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String hash = "";

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
        else
        {
            // hash the password
            hash = BCrypt.hashpw(password, BCrypt.gensalt());
        }

        try
        {
            // Establish connection to database
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Check if the customer already exists in the database
            String checkEmail = "SELECT * FROM customers WHERE email = ? AND deleteFlag = 0";
            PreparedStatement checkStmt = connection.prepareStatement(checkEmail);
            checkStmt.setString(1, email);
            ResultSet checkResult = checkStmt.executeQuery();

            if (checkResult.next()) {
                showError("Customer already exists. Please use a different email address.");
                return;
            }

            // SQL query to insert new customer into database
            String insert = "INSERT INTO customers (FirstName, SecondName, Address, Email, Password, deleteFlag) VALUES (?, ?, ?, ?, ?, ?)";

            pstat = connection.prepareStatement(insert);
            // Create prepared statement for inserting data into the table
            pstat.setString(1, name);
            pstat.setString(2, surname);
            pstat.setString(3, address);
            pstat.setString(4, email);
            pstat.setString(5, hash);
            pstat.setInt(6, 0);
            pstat.executeUpdate();

            // Retrieve customerID after successful registration
            String retrieve = "SELECT customerID FROM customers WHERE email = ?";
            // Establish connection to database
            pstat = connection.prepareStatement(retrieve);
            // Create prepared statement for retrieving data from the table
            pstat.setString(1, email);
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
