package src;

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
    Properties props = new Properties();
    String DB_URL = props.getDbUrl();
    String DB_USER = props.getDbUser();
    String DB_PASSWORD = props.getDbPassword();

    // Customer ID of the logged-in customer
    private int customerID;

    // Restaurant ID of the selected restaurant
    private int restaurantID;

    // Restaurant details
    private JLabel restaurantName;
    private JLabel cuisine;
    private JLabel rating;

    // Buttons for changing pages
    private JButton viewRestaurantsBtn;
    private JButton viewOrdersBtn;
    private JButton viewProfileBtn;

    public HomeFrame(int customerID)
    {
        this.customerID = customerID;
        setTitle("Home");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(createRestaurantPanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createRestaurantPanel()
    {
        JPanel panel = new JPanel(new MigLayout("insets 30 40 30 40, wrap 2", "[right, 100][grow, fill, 250]", "[]15[]20[]"));

        try
        {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Restaurant details retrieved from database
            String retrieve = "SELECT * FROM restaurants";

            pstat = connection.prepareStatement(retrieve);
            resultSet = pstat.executeQuery();

            while (resultSet.next())
            {
                restaurantID = resultSet.getInt("restaurantID");
                restaurantName = new JLabel(resultSet.getString("restaurantName"));
                cuisine = new JLabel(resultSet.getString("cuisine"));
                rating = new JLabel(String.valueOf(resultSet.getDouble("rating")));

                panel.add(restaurant(restaurantName, cuisine, rating), "span 2, wrap 20");
            }

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

            panel.add(viewRestaurantsBtn, "span 2, wrap 20");
            panel.add(viewOrdersBtn, "span 2, wrap 20");
            panel.add(viewProfileBtn, "span 2, wrap 20");
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
        return panel;
    }

    // Build the block for each individual restaurant
    private JPanel restaurant(JLabel restaurantName, JLabel cuisine, JLabel rating)
    {
        JPanel panel = new JPanel(new MigLayout("insets 30 40 30 40, wrap 2", "[right, 100][grow, fill, 250]", "[]15[]20[]"));

        // View menu button which will open the menu page for the selected restaurant
        JButton viewMenuBtn = new JButton("View Menu");
        viewMenuBtn.setPreferredSize(new Dimension(120, 30));
        viewMenuBtn.addActionListener(e -> {
            new Restuarant(restaurantID);
            dispose();
        });
        panel.add(restaurantName, "span 2, wrap");
        panel.add(cuisine, "span 2, wrap");
        panel.add(rating, "span 2, wrap");
        panel.add(viewMenuBtn, "span 2, wrap");
        return panel;
    }
}
