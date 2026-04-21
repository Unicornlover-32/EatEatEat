// Name: Ethan Payne
// Student ID: C00309151
// Date: 21/4/2026

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

// This will show the main home page for logged-in customer
// This will show all the available restaurants
public class HomeFrame extends JFrame
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

    // Restaurant details
    private JLabel restaurantName;
    private JLabel cuisine;
    private JLabel rating;

    // Buttons for changing pages
    private JButton viewRestaurantsBtn;
    private JButton viewOrdersBtn;
    private JButton viewProfileBtn;

    // Constructor
    public HomeFrame(int customerID)
    {
        this.customerID = customerID;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(false);
        setLayout(new MigLayout("insets 10"));
        add(createRestaurantPanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Build the home page which will show all the available restaurants
    private JPanel createRestaurantPanel()
    {
        JPanel restaurantListPanel = new JPanel(
                new MigLayout("insets 15, wrap 1, fillx", "[grow, fill]")
        );

        restaurantListPanel.setPreferredSize(new Dimension(500, 800));
        //restaurantListPanel.add(new JLabel("Restaurants"), "span 2, align center, wrap 20");
        try
        {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Restaurant details retrieved from database
            String retrieve = "SELECT * FROM restaurants";

            pstat = connection.prepareStatement(retrieve);
            resultSet = pstat.executeQuery();

            while (resultSet.next())
            {
                int currentRestaurantID = resultSet.getInt("restaurantID");
                JLabel rName = new JLabel(resultSet.getString("restaurantName"));
                JLabel rCuisine = new JLabel(resultSet.getString("cuisine"));
                JLabel rRating = new JLabel(String.valueOf(resultSet.getDouble("rating")));

                restaurantListPanel.add(restaurant(rName, rCuisine, rRating, currentRestaurantID), "growx, wrap 10");
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

        // Wrap restaurant list in scroll pane
        JScrollPane scrollPane = new JScrollPane(restaurantListPanel);
        scrollPane.setPreferredSize(new Dimension(500, 700));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        // Get rid of the horizontal scrollbar
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // Increase the scroll speed of the vertical scrollbar
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
        // Each button will open a new page and close the current page
        // No action listeners for the home button as it would open the same page
        viewOrdersBtn.addActionListener(e -> {
            // Open the order page
            new Orders(customerID);
            dispose();
        });
        viewProfileBtn.addActionListener(e -> {
            // Open the profile page
            new Profile(customerID);
            dispose();
        });

        buttonPanel.add(viewRestaurantsBtn);
        buttonPanel.add(viewOrdersBtn);
        buttonPanel.add(viewProfileBtn);

        // Main panel using MigLayout — scroll area grows, button bar is pinned to bottom
        JPanel mainPanel = new JPanel(new MigLayout("insets 0, fill, wrap 1", "[grow, fill]", "[grow, fill][]"));
        mainPanel.add(scrollPane,   "grow");
        mainPanel.add(buttonPanel,  "growx");

        return mainPanel;
    }

    // Build the block for each individual restaurant
    private JPanel restaurant(JLabel restaurantName, JLabel cuisine, JLabel rating, int restaurantID)
    {
        JPanel panel = new JPanel(new MigLayout("insets 10 15 10 15, fillx, wrap 2", "[grow, left]", "[]5[]10[]"));
        panel.setBorder(BorderFactory.createEtchedBorder());

        // Set fonts and colors for the restaurant details
        restaurantName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cuisine.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rating.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rating.setForeground(new Color(0, 120, 215));

        // Row 1: Restaurant Name and Rating
        panel.add(restaurantName, "growx");
        panel.add(rating, "right");

        // Row 2: Cuisine
        panel.add(cuisine, "span 2, growx, wrap 10");

        // Row 3: View Menu button
        JButton viewMenuBtn = new JButton("View Menu");
        //viewMenuBtn.setPreferredSize(new Dimension(120, 30));

        // Use a local final variable to capture the correct restaurantID in the action listener
        final int finalRestaurantID = restaurantID;
        viewMenuBtn.addActionListener(e -> {
            new Menus(customerID, finalRestaurantID);
            dispose();
        });

        panel.add(viewMenuBtn, "span 2, right");
        return panel;
    }
}
