package src;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class Restuarant extends JFrame
{
    public Restuarant(int restaurantID)
    {

    }

    private JPanel menuPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new MigLayout("insets 30 40 30 40, wrap 2", "[right, 100][grow, fill, 250]", "[]15[]20[]"));
        panel.add(new JLabel("Menu"));
        
        return panel;
    }
}
