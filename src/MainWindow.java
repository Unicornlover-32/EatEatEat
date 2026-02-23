package src;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow{

    public MainWindow()
    {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("EatEatEat");
        frame.setSize(500,500);

        JPanel panel = new JPanel(new MigLayout());

        String name;

        try {
            // prompt user for input
            // Menu for user options
            int option = JOptionPane.showOptionDialog(null, "Choose an option:", "Main Menu", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Create Account", "Sign In"}, null);

            if (option == 0) {
                // Gather customer details
                nameLabel = new JLabel("Enter your name");
                name = panel.add(new JTextField(10));
                surnameLabel = new JLabel("Enter your surname");
                surname = new JTextField(10);
                add(surname);
                addressLabel = new JLabel("Enter address");
                address = new JTextField(10);
                add(address);
                do
                {
                    emailLabel = new JLabel("Enter email");
                    email = new JTextField(10);
                    add(email);
                }
                while(!Verifier.verifyEmailFormat(email));
                phoneNumberLabel = new JLabel("Enter phone number");
                phoneNumber = new JTextField(10);
                add(phoneNumber);
                passwordLabel = new JLabel("Enter password");
                password = new JPasswordField(10);
                add(password);
                confirmPasswordLabel = new JLabel("Enter password again");
                confirmPassword = new JPasswordField(10);
                add(confirmPassword);

                if (password.equals(confirmPassword)) {
                    //Add customer to the database
                    Customer customer = new Customer(name, surname, address, email, phoneNumber, password);

                    // display result
                    JOptionPane.showMessageDialog(null, "Name: " + name + " " + surname +
                            "\nAddress: " + address +
                            "\nEmail: " + email +
                            "\nPhone Number: " + phoneNumber, "Customer Details", JOptionPane.PLAIN_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Passwords do not match. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (option == 1) {
                // Gather customer details
                do
                {
                    emailLabel = new JLabel("Enter email");
                    email = new JTextField(10);
                    add(email);
                }
                while(!Verifier.verifyEmailFormat(email));
                passwordLabel = new JLabel("Enter password");
                password = new JPasswordField(10);
                add(password);

                //Add customer to the database
                Login1 login = new Login1(email, password);
            }
            else {
                JOptionPane.showMessageDialog(null, "Invalid option selected.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
