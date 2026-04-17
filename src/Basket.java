package src;

import javax.swing.*;

// Basket class
// This creates the basket which can confirm the order
// Also use as an object class to store the basket items
public class Basket extends JFrame
{
    // information passed from the HomeFrame
    // will be used to store as variables to be used in the basket panel
    private int customerID;
    private int restaurantID;
    private int quantity;

    public Basket(int customerID, int restaurantID, int quantity)
    {
        this.customerID = customerID;
        this.restaurantID = restaurantID;
        this.quantity = quantity;
        setTitle("Basket");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(createBasketPanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Basket panel which will show the items in the basket and allow the customer to confirm the order
    private JPanel createBasketPanel()
    {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Basket"));

        return panel;
    }
}
