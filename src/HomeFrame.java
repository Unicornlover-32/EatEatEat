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
    Properties props = new Properties();
    String DB_URL = props.getDbUrl();
    String DB_USER = props.getDbUser();
    String DB_PASSWORD = props.getDbPassword();
    Connection connection = null;
    PreparedStatement pstat = null;
    ResultSet resultSet = null;

    // Restaurant ID of the selected restaurant
    private int restuarantID;

    // Restaurant details
    private JLabel name;
    private JLabel cuisine;
    private JLabel rating;

    public HomeFrame(int customerID)
    {
        setTitle("EatEatEat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new MigLayout("insets 10"));

        // Create a tabbed pane to switch between different panels
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Restaurants", createRestaurantPanel());
        //tabbedPane.addTab("Orders", createOrderPanel());
        //tabbedPane.addTab("Profile", createProfilePanel());

        add(tabbedPane);
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
                restuarantID = resultSet.getInt("restaurantID");
                name = new JLabel(resultSet.getString("restaurantName"));
                cuisine = new JLabel(resultSet.getString("cuisine"));
                rating = new JLabel(String.valueOf(resultSet.getDouble("rating")));

                panel.add(restuarant(name, cuisine, rating), "span 2, wrap 20");
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
        return panel;
    }

    // Build the block for each individual restaurant
    private JPanel restuarant(JLabel name, JLabel cuisine, JLabel rating)
    {
        JPanel panel = new JPanel(new MigLayout("insets 30 40 30 40, wrap 2", "[right, 100][grow, fill, 250]", "[]15[]20[]"));

        // View menu button which will open the menu page for the selected restaurant
        JButton viewMenuBtn = new JButton("View Menu");
        viewMenuBtn.setPreferredSize(new Dimension(120, 30));
        viewMenuBtn.addActionListener(e -> {
            new Restuarant(restuarantID);
            dispose();
        });
        panel.add(name, "span 2, wrap");
        panel.add(cuisine, "span 2, wrap");
        panel.add(rating, "span 2, wrap");
        panel.add(viewMenuBtn, "span 2, wrap");
        return panel;
    }
}
