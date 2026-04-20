package src;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Profile extends JFrame
{
    // Database connection properties
    private Connection connection;
    private PreparedStatement pstat;
    private ResultSet resultSet;
    Properties props = new Properties();
    String DB_URL = props.getDbUrl();
    String DB_USER = props.getDbUser();
    String DB_PASSWORD = props.getDbPassword();

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

    public Profile(int customerID)
    {
        this.customerID = customerID;
        setTitle("Profile");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(createProfilePanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new MigLayout("insets 30 40 30 40, wrap 2", "[right, 120][grow, fill, 250]", "[]15[]20[]"));

        panel.setPreferredSize(new Dimension(500, 800));

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String retrieve = "SELECT * FROM customers WHERE customerID = ?";

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

            // Add labels and text fields for customer details
            panel.add(title, "span 2, align center, wrap 15");

            panel.add(new JLabel("First Name:"));
            panel.add(firstName, "wrap");

            panel.add(new JLabel("Last Name:"));
            panel.add(lastName, "wrap");

            panel.add(new JLabel("Email:"));
            panel.add(email, "wrap");

            panel.add(new JLabel("Address:"));
            panel.add(address, "wrap");

            updateDetailsButton = new JButton("Submit");
            updateDetailsButton.addActionListener(e ->
            {
                try {
                    connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                    // Update the customer details in the database
                    String update = "UPDATE customers SET firstName = ?, lastName = ?, email = ?, address = ? WHERE customerID = ?";

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
            panel.add(updateDetailsButton);

            // Buttons to navigate to other pages
            viewRestaurantsBtn = new JButton("View Restaurants");
            viewOrdersBtn = new JButton("View Orders");
            viewProfileBtn = new JButton("View Profile");

            viewRestaurantsBtn.setPreferredSize(new Dimension(120, 30));
            viewOrdersBtn.setPreferredSize(new Dimension(120, 30));
            viewProfileBtn.setPreferredSize(new Dimension(120, 30));

            // Action listeners for the buttons
            // Each button will open a new page and close the current page
            // No action listeners for the profile button as it would open the same page
            viewRestaurantsBtn.addActionListener(e -> {
                // Open the profile page
                new HomeFrame(customerID);
                dispose();
            });
            viewOrdersBtn.addActionListener(e -> {
                // Open the order page
                new Orders(customerID);
                dispose();
            });

            panel.add(viewRestaurantsBtn);
            panel.add(viewOrdersBtn);
            panel.add(viewProfileBtn);

        }
        catch (SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        finally
        {
            try
            {
                resultSet.close();
                pstat.close();
                connection.close();
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }
        return panel;
    }
}
