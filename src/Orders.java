// Name: Ethan Payne
// Student ID: C00309151
// Date: 21/4/2026

package src;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

// Orders class to handle order-related operations
// This will show the page that lists all orders made by customers
public class Orders extends JFrame
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

    // Constructor to initialize the customer ID
    private JLabel restaurantName;
    private JLabel orderDate;
    private JLabel orderStatus;
    private JLabel orderTotal;

    // Buttons for changing pages
    private JButton viewRestaurantsBtn;
    private JButton viewOrdersBtn;
    private JButton viewProfileBtn;

    // Constructor
    public Orders(int customerID)
    {
        this.customerID = customerID;
        setTitle("Orders");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(false);
        add(createOrderPanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Build the orders panel which will show the customer's past orders and current orders
    private JPanel createOrderPanel()
    {
        JPanel orderListPanel = new JPanel(
                new MigLayout("insets 15, wrap 1, fillx", "[grow, fill]")
        );

        orderListPanel.setPreferredSize(new Dimension(500, 800));

        try
        {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Order details retrieved from database
            String retrieve = "SELECT * FROM orders o INNER JOIN restaurants r ON o.restaurantID = r.restaurantID WHERE o.customerID = ?";

            pstat = connection.prepareStatement(retrieve);
            pstat.setInt(1, customerID);

            resultSet = pstat.executeQuery();

            while (resultSet.next())
            {
                int orderId = resultSet.getInt("orderID");
                int restaurantId = resultSet.getInt("restaurantID");
                JLabel restaurantName = new JLabel(resultSet.getString("restaurantName"));
                JLabel orderStatus = new JLabel(String.valueOf(resultSet.getString("orderStatus")));
                JLabel orderTotal = new JLabel("€" + String.format("%.2f", resultSet.getDouble("orderTotal")));

                orderListPanel.add(order(orderId, restaurantId, restaurantName, orderStatus, orderTotal), "growx, wrap 10");
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
                if (resultSet != null) resultSet.close();
                if (pstat != null) pstat.close();
                if (connection != null) connection.close();
            }
            catch (SQLException sqlException)
            {
                sqlException.printStackTrace();
            }
        }

        // Wrap order list in scroll pane
        JScrollPane scrollPane = new JScrollPane(orderListPanel);
        scrollPane.setPreferredSize(new Dimension(500, 700));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);

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
        viewProfileBtn.addActionListener(e -> {
            new Profile(customerID);
            dispose();
        });

        buttonPanel.add(viewRestaurantsBtn);
        buttonPanel.add(viewOrdersBtn);
        buttonPanel.add(viewProfileBtn);

        // Main panel using MigLayout — scroll area grows, button bar is pinned to bottom
        JPanel mainPanel = new JPanel(new MigLayout("insets 0, fill, wrap 1", "[grow, fill]", "[grow, fill][]"));
        mainPanel.add(scrollPane,  "grow");
        mainPanel.add(buttonPanel, "growx");

        return mainPanel;
    }

    // Create a panel for each order
    private JPanel order(int orderId, int restaurantId, JLabel restaurantName, JLabel orderStatus, JLabel orderTotal)
    {
        JPanel panel = new JPanel(new MigLayout("insets 10 15 10 15, fillx, wrap 2", "[grow, left]", "[]5[]5[]10[]"));
        panel.setBorder(BorderFactory.createEtchedBorder());

        // Set fonts and colors for the order details
        restaurantName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        orderStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        orderStatus.setForeground(new Color(0, 120, 215));
        orderTotal.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Row 1: Restaurant Name and Status
        panel.add(restaurantName, "growx");
        panel.add(orderStatus, "right");

        // Row 2: Order Total
        panel.add(orderTotal, "span 2, growx, wrap 10");

        // Row 3: View Order button
        JButton viewOrderBtn = new JButton("View Order");
        viewOrderBtn.addActionListener(e -> {
            new ViewOrders(customerID, orderId, restaurantId ,restaurantName.getText());
            dispose();
        });

        panel.add(viewOrderBtn, "span 2, right");
        return panel;
    }
}
