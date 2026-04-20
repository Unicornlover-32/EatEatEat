package src;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class SuccessOrder extends JFrame
{
    // Variables passed from the previous frame
    private int customerId;
    private Basket[] basket;

    // calcualte total price
    private double total = 0;

    public SuccessOrder(int customerId, Basket[] basket)
    {
        this.customerId = customerId;
        this.basket = basket;
        setTitle("Order Placed Successfully");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(createSuccessPanel());

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }

    private JPanel createSuccessPanel()
    {
        JPanel successPanel = new JPanel(new MigLayout("insets 30 40 30 40, wrap 1", "[grow, fill]", "[]15[]20[]"));
        successPanel.setPreferredSize(new Dimension(500, 800));
        successPanel.add(new JLabel("Order Placed Successfully"), "span 2, align center, wrap 20");

        // Show the order details
        JLabel orderDetails = new JLabel("Order Details:");
        successPanel.add(orderDetails, "span 2, align center, wrap 20");

        for(int i = 0; i < basket.length; i++)        {
            Basket item = basket[i];
            if (item != null)
            {
                JLabel items = new JLabel(item.getItemName() + " - €" + String.format("%.2f", item.getPrice()) + " (Qty: " + item.getQuantity() + ")");
                successPanel.add(items, "wrap 20");
                total += item.getPrice() * item.getQuantity();
            }
        }

        // Show the total price
        JLabel totalPrice = new JLabel("Total Price: €" + String.format("%.2f", total));
        successPanel.add(totalPrice, "span 2, align center, wrap 20");

        // Add a button to go back to the home page
        JButton backButton = new JButton("Back to Home");
        backButton.addActionListener(e -> {
            new HomeFrame(customerId);
            dispose();
        });
        // Align the button to the bottom of the panel
        successPanel.add(backButton, "span 2, align bottom, wrap 20");

        return successPanel;
    }
}
