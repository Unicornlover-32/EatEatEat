package src.FrameBuilding;

import javax.swing.*;
import net.miginfocom.swing.MigLayout;

public class Frame extends JFrame
{
    public Frame()
    {
        setTitle("MigLayout Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set MigLayout as the layout manager
        setLayout(new MigLayout("insets 10", "[right][grow]", ""));

        // Add components using constraints
        add(new JLabel("Name:"));
        add(new JTextField(20), "wrap");  // "wrap" moves to next row

        add(new JLabel("Email:"));
        add(new JTextField(20), "wrap");

        add(new JButton("Submit"), "skip 1, align right");

        pack();
        setLocationRelativeTo(null);  // center on screen
        setVisible(true);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(Frame::new);
    }
}
