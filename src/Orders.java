package src;

// Orders class to handle order-related operations
// This will show the page that lists all orders made by customers

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Orders extends JFrame
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

    // Constructor to initialize the customer ID
    private JLabel restaurantName;
    private JLabel orderDate;
    private JLabel orderStatus;
    private JLabel orderTotal;

    // Buttons for changing pages
    private JButton viewRestaurantsBtn;
    private JButton viewOrdersBtn;
    private JButton viewProfileBtn;

    public Orders(int customerID)
    {
        this.customerID = customerID;
        setTitle("Orders");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(createOrderPanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    // Build the orders panel which will show the customer's past orders and current orders
    private JPanel createOrderPanel()
    {
        JPanel orderListPanel = new JPanel(new MigLayout("insets 30 40 10 40, wrap 1", "[grow, fill]", "[]20[]"));

        try
        {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Order details retrieved from database
            String retrieve = "SELECT * FROM orders WHERE customerID = ?";

            pstat = connection.prepareStatement(retrieve);
            pstat.setInt(1, customerID);

            resultSet = pstat.executeQuery();

            while (resultSet.next())
            {
                customerID = resultSet.getInt("customerID");
                restaurantName = new JLabel(resultSet.getString("restaurantName"));
                orderDate = new JLabel(resultSet.getString("orderDate"));
                orderStatus = new JLabel(String.valueOf(resultSet.getString("orderStatus")));
                orderTotal = new JLabel(String.format("%.2f", resultSet.getDouble("orderTotal")));

                orderListPanel.add(order(restaurantName, orderDate, orderStatus, orderTotal), "growx, wrap 20");
            }
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
            catch (SQLException sqlException)
            {
                sqlException.printStackTrace();
            }
        }

        // Wrap order list in scroll pane
        JScrollPane scrollPane = new JScrollPane(orderListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);

        // Button panel using MigLayout
        JPanel buttonPanel = new JPanel(new MigLayout("insets 10 40 20 40", "[grow, fill][grow, fill][grow, fill]", "[]"));

        // Buttons to navigate to other pages
        viewRestaurantsBtn = new JButton("View Restaurants");
        viewOrdersBtn = new JButton("View Orders");
        viewProfileBtn = new JButton("View Profile");

        viewRestaurantsBtn.setPreferredSize(new Dimension(120, 30));
        viewOrdersBtn.setPreferredSize(new Dimension(120, 30));
        viewProfileBtn.setPreferredSize(new Dimension(120, 30));

        // Action listeners for the buttons
        // Each button will open a new page and close the current page
        // No action listeners for the order button as it would open the same page
        viewRestaurantsBtn.addActionListener(e -> {
            new HomeFrame(customerID);
            dispose();
        });
        viewProfileBtn.addActionListener(e -> {
            new Profile(customerID);
            dispose();
        });

        buttonPanel.add(viewRestaurantsBtn, "growx");
        buttonPanel.add(viewOrdersBtn,      "growx");
        buttonPanel.add(viewProfileBtn,     "growx");

        // Main panel using MigLayout — scroll area grows, button bar is pinned to bottom
        JPanel mainPanel = new JPanel(new MigLayout("insets 0, fill", "[grow, fill]", "[grow, fill][]"));
        mainPanel.add(scrollPane,  "cell 0 0, grow, wrap");
        mainPanel.add(buttonPanel, "cell 0 1, growx");

        return mainPanel;
    }

    private JPanel order(JLabel restaurantName, JLabel orderDate, JLabel orderStatus, JLabel orderTotal)
    {
        JPanel panel = new JPanel(new MigLayout("insets 30 40 30 40, wrap 2", "[right, 100][grow, fill, 250]", "[]15[]20[]"));

        // View orders button which will open the order page for order selected
        JButton viewOrdersBtn = new JButton("View Orders");
        viewOrdersBtn.setPreferredSize(new Dimension(120, 30));
        viewOrdersBtn.addActionListener(e -> {

        });
        panel.add(restaurantName, "span 2, wrap");
        panel.add(orderDate, "span 2, wrap");
        panel.add(orderTotal, "span 2, wrap");
        panel.add(orderStatus, "span 2, wrap");
        panel.add(viewOrdersBtn, "span 2, wrap");
        return panel;
    }
}
