package src;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class PlaceOrder extends JFrame {
    // Database connection properties
    private Connection connection;
    private PreparedStatement pstat;
    private ResultSet resultSet;
    Properties props = new Properties();
    String DB_URL = props.getDbUrl();
    String DB_USER = props.getDbUser();
    String DB_PASSWORD = props.getDbPassword();

    // Variables passed from the previous panel
    private int customerID;
    private int restaurantID;
    private Basket[] basket;

    // Total basket price
    private double total = 0;

    // Buttons
    private JButton placeOrderBtn;

    public PlaceOrder(int customerID, int restaurantID, Basket[] basket)
    {
        this.customerID = customerID;
        this.restaurantID = restaurantID;
        this.basket = basket;

        setTitle("Place Order");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new JLabel("Place Order Page for Customer ID: " + customerID + " and Restaurant ID: " + restaurantID));
        add(createBasketPanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }

    private JPanel createBasketPanel()
    {
        JPanel basketPanel = new JPanel(new MigLayout("insets 30 40 30 40, wrap 1", "[grow, fill]", "[]15[]20[]"));
        basketPanel.add(new JLabel("Your Basket"), "span 2, align center, wrap 20");

        for(int i = 0; i < basket.length; i++)
        {
            Basket item = basket[i];
            if (item != null)
            {
                basketPanel.add(new JLabel(item.getItemName() + " - €" + item.getPrice() + " (Qty: " + item.getQuantity() + ")"));
                total += item.getPrice() * item.getQuantity();
            }
        }

        basketPanel.add(new JLabel("Total: €" + String.format("%.2f", total)), "align right");

        placeOrderBtn = new JButton("Place Order");
        placeOrderBtn.addActionListener(e -> {
            orderPlaceSQL();
            // Send to screen showing order placed
            new SuccessOrder(customerID, basket);
            dispose();
        });
        basketPanel.add(placeOrderBtn, "align right");

        return basketPanel;
    }

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
                if (basket != null)
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

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
