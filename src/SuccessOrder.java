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
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(false);
        add(createSuccessPanel());

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createSuccessPanel()
    {
        JPanel mainPanel = new JPanel(new MigLayout("insets 0, fill, wrap 1", "[grow, fill]", "[grow, fill][]"));
        mainPanel.setPreferredSize(new Dimension(500, 800));

        JPanel successPanel = new JPanel(new MigLayout("insets 30 40 30 40, wrap 1", "[grow, fill]", "[]15[]20[]"));

        JLabel title = new JLabel("Order Placed Successfully");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        successPanel.add(title, "align center, wrap 20");

        // Show the order details
        JLabel orderDetails = new JLabel("Order Details:");
        orderDetails.setFont(new Font("Segoe UI", Font.BOLD, 14));
        successPanel.add(orderDetails, "align center, wrap 20");

        for(int i = 0; i < basket.length; i++) {
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

                successPanel.add(itemPanel, "growx, wrap 10");
                total += item.getPrice() * item.getQuantity();
            }
        }

        // Show the total price
        JLabel totalPrice = new JLabel("Total Price: €" + String.format("%.2f", total));
        totalPrice.setFont(new Font("Segoe UI", Font.BOLD, 16));
        successPanel.add(totalPrice, "right, wrap 20");

        JScrollPane scrollPane = new JScrollPane(successPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Add a button to go back to the home page
        JPanel buttonPanel = new JPanel(new MigLayout("insets 10 40 20 40, fillx", "[grow, fill]"));
        buttonPanel.setPreferredSize(new Dimension(500, 100));

        JButton backButton = new JButton("Back to Home");
        backButton.addActionListener(e -> {
            new HomeFrame(customerId);
            dispose();
        });
        buttonPanel.add(backButton, "growx");

        mainPanel.add(scrollPane, "grow");
        mainPanel.add(buttonPanel, "growx");

        return mainPanel;
    }
}
