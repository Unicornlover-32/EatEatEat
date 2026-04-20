// Name: Ethan Payne
// Student ID: C00309151
// Date: 21/4/2026

package src;

import net.miginfocom.swing.MigLayout;
import org.apache.commons.validator.routines.EmailValidator;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

// Login class to handle login-related operations
// This will show the login page and allow the customer to log in
class Login extends JFrame
{
    // Database connection properties
    private Connection connection;
    private PreparedStatement pstat;
    private ResultSet resultSet;
    private Properties props = new Properties();
    private String DB_URL = props.getDbUrl();
    private String DB_USER = props.getDbUser();
    private String DB_PASSWORD = props.getDbPassword();

    // Login fields
    private JTextField loginEmailField;
    private JPasswordField loginPasswordField;

    // Verifier
    private Verifier v = new Verifier();

    // Constructor
    public Login()
    {
        setTitle("EatEatEat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(false);
        setLayout(new MigLayout("insets 10"));
        add(createLoginPanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Retrieve Customer ID
    private int customerID = -1;

    // create login panel
    public JPanel createLoginPanel()
    {
        JPanel panel = new JPanel(new MigLayout("insets 30 40 30 40, wrap 1, fillx", "[grow, fill]"));
        panel.setPreferredSize(new Dimension(500, 800));

        JLabel title = new JLabel("Sign In");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        panel.add(title, "align center, wrap 30");

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(emailLabel, "wrap 5");
        loginEmailField = new JTextField();
        loginEmailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(loginEmailField, "growx, wrap 15");

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(passwordLabel, "wrap 5");
        loginPasswordField = new JPasswordField();
        loginPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(loginPasswordField, "growx, wrap 25");

        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setPreferredSize(new Dimension(150, 30));
        loginBtn.addActionListener(e -> handleLogin());
        panel.add(loginBtn, "growx, wrap 20");

        JButton registerBtn = new JButton("Create Account");
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerBtn.setPreferredSize(new Dimension(150, 30));
        registerBtn.addActionListener(e -> {
            new Account();
            dispose();
        });
        panel.add(registerBtn, "growx");

        return panel;
    }

    // Handle login logic
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

            // SQL query to retrieve the hashed password for the given email
            String retrieve = "SELECT customerID, password FROM customers WHERE email = ? AND deleteFlag = 0";

            pstat = connection.prepareStatement(retrieve);
            pstat.setString(1, email);
            resultSet = pstat.executeQuery();

            if (resultSet.next())
            {
                // password verification using BCrypt
                String storedHashedPassword = resultSet.getString("password");
                boolean isMatch = BCrypt.checkpw(password, storedHashedPassword);

                if (isMatch)
                {
                    customerID = resultSet.getInt("customerID");
                }
                else
                {
                    showError("Invalid email or password. Please try again.");
                }
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

    // Show error message
    private void showError(String message)
    {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Open home frame after successful login
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