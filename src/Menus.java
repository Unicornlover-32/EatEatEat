package src;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Menus extends JFrame
{
    // Database connection properties
    private Connection connection;
    private PreparedStatement pstat;
    private ResultSet resultSet;
    Properties props = new Properties();
    String DB_URL = props.getDbUrl();
    String DB_USER = props.getDbUser();
    String DB_PASSWORD = props.getDbPassword();

    // Restaurant ID of the selected restaurant
    private int restaurantID;

    // Customer ID of the logged-in customer
    private int customerID;

    public Menus(int restaurantID, int customerID)
    {
        this.restaurantID = restaurantID;
        this.customerID = customerID;
        setTitle("EatEatEat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new MigLayout("insets 10"));
        add(createMenuPanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Build the menu panel which will show the menu of the selected restaurant
    private JPanel createMenuPanel()
    {
        JPanel menuListPanel = new JPanel();
        try
        {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            // Menu details retrieved from database
            String retrieve = "SELECT * FROM menus WHERE restaurantID = ?";

            System.out.println("Retrieving menu items for restaurant ID: " + restaurantID);

            // Establish connection to database
            pstat = connection.prepareStatement(retrieve);
            pstat.setInt(1, restaurantID);
            resultSet = pstat.executeQuery();

            // display each menu item in a separate label
            while (resultSet.next())
            {
                JLabel menuItem = new JLabel(resultSet.getString("itemName"));
                JLabel price = new JLabel(String.format("%.2f", resultSet.getDouble("itemPrice")));
                JLabel description = new JLabel(resultSet.getString("itemDescription"));

                menuListPanel.add(menu(menuItem, price, description), "span 2, wrap 20");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return menuListPanel;
    }

    // Build the block for each individual menu item
    private JPanel menu(JLabel menuItem, JLabel price, JLabel description)
    {
        JPanel menuItemPanel = new JPanel(new MigLayout("insets 30 40 30 40, wrap 2", "[right, 100][grow, fill, 250]", "[]15[]20[]"));

        // add the menu item and price to the panel
        menuItemPanel.add(menuItem);
        menuItemPanel.add(price);

        // create a selector for each menu item to be able to order it
        JComboBox<String> quantitySelector = new JComboBox<>(new String[] {"1", "2", "3", "4", "5"});
        menuItemPanel.add(quantitySelector, "align right, wrap 20");
        // add a button to add the selected menu item to the Basket
        JButton addToBasketButton = new JButton("Add to Basket");
        addToBasketButton.addActionListener(e -> {
            // code to add the selected menu item and quantity to the Basket
            int selectedQuantity = (int) quantitySelector.getSelectedItem();
            // code to add the menu item and quantity to the Basket in the database
            new Basket(restaurantID, customerID, selectedQuantity);
        });
        // add the button to the panel
        menuItemPanel.add(addToBasketButton, "wrap 20, align right");
        // add the description of the menu item to the panel
        menuItemPanel.add(description, "span 2, wrap 20");

        return menuItemPanel;
    }
}
