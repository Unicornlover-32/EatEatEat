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

    // Basket object
    final int MAX_ITEMS = 20;
    Basket[] basket = new Basket[MAX_ITEMS]; // Assuming a maximum of 20 items in the basket for simplicity

    // Counter for button presses
    private int buttonPresses = 0;

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
        setResizable(false);
    }

    // Build the menu panel which will show the menu of the selected restaurant
    private JPanel createMenuPanel()
    {
        JPanel menuListPanel = new JPanel(new MigLayout("insets 30 40 30 40, wrap 2", "[right, 100][grow, fill, 250]", "[]15[]20[]"));

        menuListPanel.setPreferredSize(new Dimension(500, 800));

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
                int itemID = resultSet.getInt("itemID");
                JLabel menuItem = new JLabel(resultSet.getString("itemName"));
                JLabel price = new JLabel(String.format("%.2f", resultSet.getDouble("itemPrice")));
                JLabel description = new JLabel(resultSet.getString("itemDescription"));

                menuListPanel.add(menu(itemID, menuItem, price, description), "span 2, wrap 20");
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
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        // Get rid of the horizontal scrollbar
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // Increase the scroll speed of the vertical scrollbar
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(500, 650));

        // Button panel using MigLayout
        JPanel buttonPanel = new JPanel(new MigLayout("insets 10 40 20 40", "[][]", "[]"));
        buttonPanel.setPreferredSize(new Dimension(500, 150));
        // View basket button
        JButton viewBasketButton = new JButton("View Basket");
        viewBasketButton.addActionListener(e -> {
            // code to open the Basket page and display the items in the basket
            new PlaceOrder(customerID, restaurantID, basket);
            dispose();
        });
        buttonPanel.add(viewBasketButton, "span 2, wrap 20");

        // Main panel using MigLayout — scroll area grows, button bar is pinned to bottom
        JPanel mainPanel = new JPanel(new MigLayout("insets 0, fill", "[grow, fill]", "[grow, fill][]"));
        mainPanel.add(scrollPane,   "cell 0 0, grow, wrap");
        mainPanel.add(buttonPanel,  "cell 0 1, growx");

        return mainPanel;
    }

    // Build the block for each individual menu item
    private JPanel menu(int itemID, JLabel menuItem, JLabel price, JLabel description)
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
            int selectedQuantity = Integer.parseInt((String) quantitySelector.getSelectedItem());

            //
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
        // add the button to the panel
        menuItemPanel.add(addToBasketButton, "wrap 20, align right");
        // add the description of the menu item to the panel
        menuItemPanel.add(description, "span 2, wrap 20");

        return menuItemPanel;
    }
}
