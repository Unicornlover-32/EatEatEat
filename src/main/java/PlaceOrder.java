// Name: Ethan Payne
// Student ID: C00309151
// Date: 21/4/2026

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

// This will show the page where the customer can place an order.
// It will show the basket items and the total price.
public class PlaceOrder extends JFrame {
    // Database connection properties
    private Connection connection;
    private PreparedStatement pstat;
    private ResultSet resultSet;
    private Properties props = new Properties();
    private String DB_URL = props.getDbUrl();
    private String DB_USER = props.getDbUser();
    private String DB_PASSWORD = props.getDbPassword();

    // Variables passed from the previous panel
    private int customerID;
    private int restaurantID;
    private Basket[] basket;

    // Total basket price
    private double total = 0;

    // Buttons
    private JButton placeOrderBtn;

    // Constructor
    public PlaceOrder(int customerID, int restaurantID, Basket[] basket)
    {
        this.customerID = customerID;
        this.restaurantID = restaurantID;
        this.basket = basket;

        setTitle("Place Order");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(false);
        add(createBasketPanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Build the panel which will show the basket items and the total price
    private JPanel createBasketPanel()
    {
        JPanel basketPanel = new JPanel(new MigLayout("insets 15, wrap 1, fillx", "[grow, fill]"));

        JPanel basketListPanel = new JPanel(new MigLayout("insets 15, wrap 1, fillx", "[grow, fill]"));
        basketListPanel.setPreferredSize(new Dimension(500, 800));

        JLabel title = new JLabel("Your Basket");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        basketListPanel.add(title, "align center, wrap 20");

        // Loop through the basket items and display them
        for(int i = 0; i < basket.length; i++)
        {
            Basket item = basket[i];
            if (item != null)
            {
                JPanel itemPanel = new JPanel(new MigLayout("insets 10 15 10 15, fillx, wrap 2", "[grow, left]", "[]"));
                itemPanel.setBorder(BorderFactory.createEtchedBorder());

                JLabel itemName = new JLabel(item.getItemName());
                itemName.setFont(new Font("Segoe UI", Font.BOLD, 14));

                JLabel itemQty = new JLabel("Qty: " + item.getQuantity());
                itemQty.setFont(new Font("Segoe UI", Font.PLAIN, 12));

                JLabel itemPrice = new JLabel("€" + String.format("%.2f", item.getPrice() * item.getQuantity()));
                itemPrice.setFont(new Font("Segoe UI", Font.BOLD, 13));
                itemPrice.setForeground(new Color(0, 120, 215));

                itemPanel.add(itemName, "growx");
                itemPanel.add(itemPrice, "right, wrap");
                itemPanel.add(itemQty, "span 2");

                basketListPanel.add(itemPanel, "growx, wrap 10");
                total += item.getPrice() * item.getQuantity();
            }
        }

        // Show the total price
        JLabel totalLabel = new JLabel("Total: €" + String.format("%.2f", total));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        basketListPanel.add(totalLabel, "right, wrap 20");

        // Create a scrollable panel for the basket list
        JScrollPane scrollPane = new JScrollPane(basketListPanel);
        scrollPane.setPreferredSize(new Dimension(500, 700));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        // Get rid of the horizontal scrollbar
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);

        // Button panel using MigLayout
        JPanel buttonPanel = new JPanel(new MigLayout("insets 10 40 20 40, fillx, wrap 1", "[grow, fill]"));
        buttonPanel.setPreferredSize(new Dimension(500, 100));

        placeOrderBtn = new JButton("Place Order");
        placeOrderBtn.addActionListener(e -> {
            orderPlaceSQL();
            // Send to screen showing order placed
            new SuccessOrder(customerID, basket);
            dispose();
        });
        buttonPanel.add(placeOrderBtn, "growx");

        // Button to go back to the menu page
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            new Menus(customerID, restaurantID);
            dispose();
        });
        buttonPanel.add(backButton, "growx");

        // Main panel using MigLayout — scroll area grows, button bar is pinned to bottom
        JPanel mainPanel = new JPanel(new MigLayout("insets 0, fill, wrap 1", "[grow, fill]", "[grow, fill][]"));
        mainPanel.add(scrollPane, "grow");
        mainPanel.add(buttonPanel, "growx");

        return mainPanel;
    }

    // SQL query to insert order details into the database
    // Stored as a method
    private void orderPlaceSQL()
    {
        try
        {
            // Establish connection to database
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Insert order details into the database
            String insertOrder = "INSERT INTO orders (customerID, restaurantID, orderTotal, orderStatus) VALUES (?, ?, ?, ?)";
            pstat = connection.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS);
            pstat.setInt(1, customerID);
            pstat.setInt(2, restaurantID);
            pstat.setDouble(3, total);
            pstat.setString(4, "Pending");
            pstat.executeUpdate();

            // retrieve the orderID
            String retrieveOrderID = "SELECT LAST_INSERT_ID() AS orderID";
            pstat = connection.prepareStatement(retrieveOrderID);
            resultSet = pstat.executeQuery();
            int orderID = 0;
            if (resultSet.next())
            {
                orderID = resultSet.getInt("orderID");
            }

            String insertOrderDetails = "INSERT INTO orderItems (orderID, itemID, quantity) VALUES (?, ?, ?)";

            for (int i = 0; i < basket.length; i++)
            {
                if (basket[i] != null)
                {
                    pstat = connection.prepareStatement(insertOrderDetails);
                    pstat.setInt(1, orderID); // Get the generated orderID
                    pstat.setInt(2, basket[i].getItemID());
                    pstat.setInt(3, basket[i].getQuantity());
                    pstat.executeUpdate();
                }
            }
        }
        catch (SQLException sqlException)
        {
            sqlException.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while connecting to the database. Please try again later.", "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally
        {
            try
            {
                connection.close();
                pstat.close();
                resultSet.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
