// Name: Ethan Payne
// Student ID: C00309151
// Date: 21/4/2026

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// This will show the page that lists all the available menus of the selected restaurant
// It will also allow the customer to add items to the basket
public class Menus extends JFrame
{
    // Database connection properties
    private Connection connection;
    private PreparedStatement pstat;
    private ResultSet resultSet;
    private Properties props = new Properties();
    private String DB_URL = props.getDbUrl();
    private String DB_USER = props.getDbUser();
    private String DB_PASSWORD = props.getDbPassword();

    // Restaurant ID of the selected restaurant
    private int restaurantID;

    // Customer ID of the logged-in customer
    private int customerID;

    // Basket object
    final int MAX_ITEMS = 20;
    Basket[] basket = new Basket[MAX_ITEMS]; // Assuming a maximum of 20 items in the basket for simplicity

    // Counter for button presses
    private int buttonPresses = 0;

    // Constructor
    public Menus(int customerID, int restaurantID)
    {
        this.customerID = customerID;
        this.restaurantID = restaurantID;
        setTitle("EatEatEat");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(false);
        setLayout(new MigLayout("insets 10"));
        add(createMenuPanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Build the menu panel which will show the menu of the selected restaurant
    private JPanel createMenuPanel()
    {
        //JPanel menuListPanel = new JPanel(new MigLayout("insets 15 20 15 20, wrap 1, fillx", "[grow, fill]"));
        JPanel menuListPanel = new JPanel(
                new MigLayout("insets 15, wrap 1, fillx", "[grow, fill]")
        );

        try
        {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            // Menu details retrieved from database
            String retrieve = "SELECT * FROM menus WHERE restaurantID = ?";

            // Establish connection to database
            pstat = connection.prepareStatement(retrieve);
            pstat.setInt(1, restaurantID);
            resultSet = pstat.executeQuery();

            // display each menu item in a separate label
            while (resultSet.next())
            {
                int itemID = resultSet.getInt("itemID");
                JLabel menuItem = new JLabel("<html><body style='width: 300px'>" + resultSet.getString("itemName") + "</body></html>");
                JLabel price = new JLabel(String.format("%.2f", resultSet.getDouble("itemPrice")));
                JLabel description = new JLabel("<html><body style='width: 300px'>" + resultSet.getString("itemDescription") + "</body></html>");

                //menuListPanel.add(menu(itemID, menuItem, price, description), "span 2, wrap 10");
                menuListPanel.add(menu(itemID, menuItem, price, description), "growx, wrap 10");
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                resultSet.close();
                pstat.close();
                connection.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        JScrollPane scrollPane = new JScrollPane(menuListPanel);
        scrollPane.setPreferredSize(new Dimension(500, 700));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        // Get rid of the horizontal scrollbar
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // Increase the scroll speed of the vertical scrollbar
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(500, 700));

        // Button panel using MigLayout
        JPanel buttonPanel = new JPanel(new MigLayout("insets 10 40 20 40, fillx, wrap 1", "[grow, fill]"));
        buttonPanel.setPreferredSize(new Dimension(500, 100));
        // View basket button
        JButton viewBasketButton = new JButton("View Basket");
        viewBasketButton.addActionListener(e -> {
            // code to open the Basket page and display the items in the basket
            new PlaceOrder(customerID, restaurantID, basket);
            dispose();
        });
        buttonPanel.add(viewBasketButton, "growx");

        // Back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            // Clear the basket when going back to the restaurant list
            for(int i = 0; i < MAX_ITEMS; i++)
            {
                basket[i] = null;
            }
            new HomeFrame(customerID);
            dispose();
        });
        buttonPanel.add(backButton, "growx");

        // Main panel using MigLayout — scroll area grows, button bar is pinned to bottom
        JPanel mainPanel = new JPanel(new MigLayout("insets 0, fill, wrap 1", "[grow, fill]", "[grow, fill][]"));
        mainPanel.add(scrollPane, "grow");
        mainPanel.add(buttonPanel, "growx");

        return mainPanel;
    }

    // Build the block for each individual menu item
    private JPanel menu(int itemID, JLabel menuItem, JLabel price, JLabel description)
    {
        JPanel menuItemPanel = new JPanel(new MigLayout("insets 10 15 10 15, fillx, wrap 2", "[grow, left]", "[]5[]10[]"));
        menuItemPanel.setBorder(BorderFactory.createEtchedBorder());

        // Set fonts and colors for the menu item details
        menuItem.setFont(new Font("Segoe UI", Font.BOLD, 14));
        description.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        price.setFont(new Font("Segoe UI", Font.BOLD, 13));
        price.setForeground(new Color(0, 120, 215));

        // Row 1: Item Name and Price
        menuItemPanel.add(menuItem, "growx");
        menuItemPanel.add(price, "right");

        // Row 2: Description (spans both columns)
        menuItemPanel.add(description, "span 2, growx, wrap 10");

        // Row 3: Quantity selector and Add to Basket button
        JComboBox<String> quantitySelector = new JComboBox<>(new String[] {"1", "2", "3", "4", "5"});
        JButton addToBasketButton = new JButton("Add to Basket");

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);
        actionPanel.add(new JLabel("Qty:"));
        actionPanel.add(quantitySelector);
        actionPanel.add(addToBasketButton);

        menuItemPanel.add(actionPanel, "span 2, right");

        addToBasketButton.addActionListener(e -> {
            // code to add the selected menu item and quantity to the Basket
            int selectedQuantity = Integer.parseInt((String) quantitySelector.getSelectedItem());

            // store item details in the Basket class
            // stored as objects
            String itemName = menuItem.getText();
            double itemPrice = Double.parseDouble(price.getText());
            if (buttonPresses < MAX_ITEMS)
            {
                basket[buttonPresses] = new Basket(customerID, restaurantID, itemID, selectedQuantity, itemName, itemPrice);
                buttonPresses++;
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Basket is full!");
            }
        });

        return menuItemPanel;
    }
}
