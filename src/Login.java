package src;

import net.miginfocom.swing.MigLayout;
import org.apache.commons.validator.routines.EmailValidator;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

class Login extends JFrame
{
    // Database connection properties
    private Connection connection;
    private PreparedStatement pstat;
    private ResultSet resultSet;
    Properties props = new Properties();
    String DB_URL = props.getDbUrl();
    String DB_USER = props.getDbUser();
    String DB_PASSWORD = props.getDbPassword();

    // Login fields
    private JTextField loginEmailField;
    private JPasswordField loginPasswordField;

    // Buttons
    private JButton loginBtn;
    private JButton registerBtn;

    // Constructor
    public Login()
    {
        setTitle("EatEatEat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new MigLayout("insets 10"));
        add(createLoginPanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Retrieve Customer ID
    private int customerID = -1;

    // login panel
    public JPanel createLoginPanel()
    {
        JPanel panel = new JPanel(new MigLayout("insets 30 40 30 40, wrap 2", "[right, 100][grow, fill, 250]", "[]15[]20[]"));

        JLabel title = new JLabel("Sign In");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(title, "span 2, align center, wrap 20");

        // Login button is not needed on the registration page, but we can keep it for consistency
        // The button does nothing as it isnt needed
        loginBtn = new JButton("Login");
        loginBtn.setPreferredSize(new Dimension(150, 30));

        // Register button to open the registration page
        registerBtn = new JButton("Register");
        registerBtn.setPreferredSize(new Dimension(150, 30));
        registerBtn.addActionListener(e -> {
            new Account();
            dispose();
        });

        panel.add(loginBtn);
        panel.add(registerBtn);

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