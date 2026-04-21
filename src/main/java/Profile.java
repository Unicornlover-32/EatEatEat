// Name: Ethan Payne
// Student ID: C00309151
// Date: 21/4/2026

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

// Profile class to handle profile-related operations
// This will show the customer's profile details and allow them to update their details
// It will also allow them to delete their account
public class Profile extends JFrame
{
    // Database connection properties
    private Connection connection;
    private PreparedStatement pstat;
    private ResultSet resultSet;
    private Properties props = new Properties();
    private String DB_URL = props.getDbUrl();
    private String DB_USER = props.getDbUser();
    private String DB_PASSWORD = props.getDbPassword();

    // Customer ID of the logged-in customer
    private int customerID;

    // Customer details
    private JTextField firstName;
    private JTextField lastName;
    private JTextField email;
    private JTextField address;

    // Button to update customer details
    private JButton updateDetailsButton;

    // Buttons for changing pages
    private JButton viewRestaurantsBtn;
    private JButton viewOrdersBtn;
    private JButton viewProfileBtn;

    // Constructor
    public Profile(int customerID)
    {
        this.customerID = customerID;
        setTitle("Profile");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(false);
        add(createProfilePanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Build the profile panel which will show the customer's profile details'
    private JPanel createProfilePanel() {
        // Main panel using MigLayout — scroll area grows, button bar is pinned to bottom
        JPanel mainPanel = new JPanel(new MigLayout("insets 0, fill, wrap 1", "[grow, fill]", "[grow, fill][]"));
        mainPanel.setPreferredSize(new Dimension(500, 800));

        // Profile details panel
        JPanel panel = new JPanel(new MigLayout("insets 30 40 30 40, wrap 1, fillx", "[grow, fill]"));

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // SQL query to retrieve customer details from the database
            String retrieve = "SELECT * FROM customers WHERE customerID = ?";

            // Establish connection to database
            pstat = connection.prepareStatement(retrieve);
            pstat.setInt(1, customerID);
            resultSet = pstat.executeQuery();

            // Retrieve customer details from the database
            if (resultSet.next()) {
                // Set text fields to allow editing
                firstName = new JTextField(resultSet.getString("firstName"));
                lastName = new JTextField(resultSet.getString("secondName"));
                email = new JTextField(resultSet.getString("email"));
                address = new JTextField(resultSet.getString("address"));
            }

            // Page title
            JLabel title = new JLabel("Profile");
            title.setFont(new Font("Segoe UI", Font.BOLD, 18));
            panel.add(title, "align center, wrap 20");

            // Helper for form fields
            addFormField(panel, "First Name:", firstName);
            addFormField(panel, "Last Name:", lastName);
            addFormField(panel, "Email:", email);
            addFormField(panel, "Address:", address);

            // Update details button
            updateDetailsButton = new JButton("Update Details");
            updateDetailsButton.addActionListener(e ->
            {
                try {
                    connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                    // Update the customer details in the database
                    String update = "UPDATE customers SET firstName = ?, secondName = ?, email = ?, address = ? WHERE customerID = ?";

                    // Create a prepared statement for updating the customer details
                    pstat = connection.prepareStatement(update);
                    pstat.setString(1, firstName.getText());
                    pstat.setString(2, lastName.getText());
                    pstat.setString(3, email.getText());
                    pstat.setString(4, address.getText());
                    pstat.setInt(5, customerID);
                    pstat.executeUpdate();

                    // Show a success message or perform any other necessary actions
                    JOptionPane.showMessageDialog(null, "Customer details updated successfully!");
                }
                catch (SQLException sqlException)
                {
                    sqlException.printStackTrace();
                }
            });
            panel.add(updateDetailsButton, "growx, wrap 15");

            // Delete account button
            JButton deleteAccountButton = new JButton("Delete Account");
            deleteAccountButton.setForeground(Color.RED);
            deleteAccountButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete your account?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                        // Delete the customer account from the database
                        String delete = "UPDATE customers SET deleteFlag = 1 WHERE customerID = ?";

                        // Create a prepared statement for deleting the customer account
                        pstat = connection.prepareStatement(delete);
                        pstat.setInt(1, customerID);
                        pstat.executeUpdate();

                        // Show a success message or perform any other necessary actions
                        JOptionPane.showMessageDialog(null, "Account deleted successfully!");
                        // Redirect to login page or close the application
                        new Login();
                        dispose();
                    } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                    }
                }
            });

            panel.add(deleteAccountButton, "growx, wrap 20");

            // Scroll Pane for the form
            JScrollPane scrollPane = new JScrollPane(panel);
            scrollPane.setBorder(null);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);

            // Button panel using MigLayout
            JPanel buttonPanel = new JPanel(new MigLayout("insets 10 40 20 40, fillx", "[grow, fill][grow, fill][grow, fill]", "[]"));
            buttonPanel.setPreferredSize(new Dimension(500, 100));

            // Buttons to navigate to other pages
            viewRestaurantsBtn = new JButton("View Restaurants");
            viewOrdersBtn = new JButton("View Orders");
            viewProfileBtn = new JButton("View Profile");

            viewRestaurantsBtn.setPreferredSize(new Dimension(120, 30));
            viewOrdersBtn.setPreferredSize(new Dimension(120, 30));
            viewProfileBtn.setPreferredSize(new Dimension(120, 30));

            // Action listeners for the buttons
            viewRestaurantsBtn.addActionListener(e -> {
                new HomeFrame(customerID);
                dispose();
            });
            viewOrdersBtn.addActionListener(e -> {
                new Orders(customerID);
                dispose();
            });

            buttonPanel.add(viewRestaurantsBtn);
            buttonPanel.add(viewOrdersBtn);
            buttonPanel.add(viewProfileBtn);

            mainPanel.add(scrollPane, "grow");
            mainPanel.add(buttonPanel, "growx");
        }
        catch (SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        finally
        {
            try
            {
                if (resultSet != null) resultSet.close();
                if (pstat != null) pstat.close();
                if (connection != null) connection.close();
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }
        return mainPanel;
    }

    // Method to add form fields to the panel
    // Reduces the repetition of styling and layout for each field
    private void addFormField(JPanel panel, String labelText, JTextField textField) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(label, "wrap 5");
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(textField, "growx, wrap 15");
    }
}
