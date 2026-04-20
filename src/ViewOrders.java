// Name: Ethan Payne
// Student ID: C00309151
// Date: 21/4/2026

package src;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

// This will show an order from the previous frame which has been selected by the customer.
// It will show the order details and the restaurant name, and the order status.
public class ViewOrders extends JFrame
{
    // Database connection properties
    private Connection connection;
    private PreparedStatement pstat;
    private ResultSet resultSet;
    private Properties props = new Properties();
    private String DB_URL = props.getDbUrl();
    private String DB_USER = props.getDbUser();
    private String DB_PASSWORD = props.getDbPassword();

    // Variables passed from the previous frame
    private int customerId;
    private int orderId;
    private int restaurantId;
    private String restaurantName;

    // Total price of the order
    private double total = 0;

    // Constructor
    public ViewOrders(int customerId, int orderId, int restaurantId, String restaurantName)
    {
        this.customerId = customerId;
        this.orderId = orderId;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        setTitle("EatEatEat");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        add(createViewingOrdersPanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Build the panel which will show the order details
    private JPanel createViewingOrdersPanel()
    {
        JPanel mainPanel = new JPanel(new MigLayout("insets 0, fill, wrap 1", "[grow, fill]", "[grow, fill][]"));
        mainPanel.setPreferredSize(new Dimension(500, 800));

        JPanel orderItemsPanel = new JPanel(new MigLayout("insets 30 40 30 40, wrap 1", "[grow, fill]", "[]15[]20[]"));

        JLabel orderDetails = new JLabel("Order Details:");
        orderDetails.setFont(new Font("Segoe UI", Font.BOLD, 14));
        orderItemsPanel.add(orderDetails, "align center, wrap 20");

        try
        {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Retrieve the order details from the database using a JOIN query to get the item names, quantities, and prices
            String retrieve = "SELECT m.itemName, oi.quantity, m.itemPrice FROM orderItems oi INNER JOIN menus m ON oi.itemID = m.itemID INNER JOIN orders o ON oi.orderID = o.orderID WHERE o.customerID = ? AND o.orderID = ?";

            // Establish connection to database
            pstat = connection.prepareStatement(retrieve);
            pstat.setInt(1, customerId);
            pstat.setInt(2, orderId);
            resultSet = pstat.executeQuery();

            // Display each item in the order in a separate panel with the item name, quantity, and price
            while (resultSet.next()) {
                String itemName = resultSet.getString("itemName");
                int quantity = resultSet.getInt("quantity");
                double price = resultSet.getDouble("itemPrice");

                JPanel itemPanel = new JPanel(new MigLayout("insets 10 15 10 15, fillx, wrap 2", "[grow, left]", "[]"));
                itemPanel.setBorder(BorderFactory.createEtchedBorder());

                JLabel itemNameLabel = new JLabel(itemName);
                itemNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

                JLabel itemQtyLabel = new JLabel("Qty: " + quantity);
                itemQtyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

                JLabel itemPriceLabel = new JLabel("€" + String.format("%.2f", price * quantity));
                itemPriceLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
                itemPriceLabel.setForeground(new Color(0, 120, 215));

                total += price * quantity;

                itemPanel.add(itemNameLabel, "growx");
                itemPanel.add(itemPriceLabel, "right, wrap");
                itemPanel.add(itemQtyLabel, "span 2");

                orderItemsPanel.add(itemPanel, "growx, wrap 10");
            }

            // show the total price
            JLabel totalPrice = new JLabel("Total Price: €" + String.format("%.2f", total));
            totalPrice.setFont(new Font("Segoe UI", Font.BOLD, 16));
            orderItemsPanel.add(totalPrice, "right, wrap 20");

            JScrollPane scrollPane = new JScrollPane(orderItemsPanel);
            scrollPane.setBorder(null);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);

            // Add a button to go back to the home page
            JPanel buttonPanel = new JPanel(new MigLayout("insets 10 40 20 40, fillx", "[grow, fill]"));
            buttonPanel.setPreferredSize(new Dimension(500, 100));

            JButton backButton = new JButton("Back to Home");
            backButton.addActionListener(e -> {
                new Orders(customerId);
                dispose();
            });
            buttonPanel.add(backButton, "growx");

            mainPanel.add(scrollPane, "grow");
            mainPanel.add(buttonPanel, "growx");

            return mainPanel;
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog(this, "Failed to connect to the database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        finally
        {
            try
            {
                resultSet.close();
                pstat.close();
                connection.close();
            }
            catch (SQLException ex)
            {
                JOptionPane.showMessageDialog(this, "Failed to close database resources: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
