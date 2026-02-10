package src;

import javax.swing.JOptionPane; // program uses JOptionPane

public class MainWindow {

    public static void main( String [] args )
    {
        String email;

        try {
            // prompt user for input
            // Menu for user options
            int option = JOptionPane.showOptionDialog(null, "Choose an option:", "Main Menu", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Create Account", "Sign In"}, null);

            if (option == 0) {
                // Gather customer details
                String name = JOptionPane.showInputDialog("Enter your name");
                String surname = JOptionPane.showInputDialog("Enter your surname");
                String address = JOptionPane.showInputDialog("Enter address");
                do
                {
                    email = JOptionPane.showInputDialog("Enter email");
                }
                while(!Verifier.verifyEmailFormat(email));
                String phoneNumber = JOptionPane.showInputDialog("Enter phone number");
                String password = JOptionPane.showInputDialog("Enter password");
                String confirmPassword = JOptionPane.showInputDialog("Enter password again");

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
                    email = JOptionPane.showInputDialog("Enter email");
                }
                while(!Verifier.verifyEmailFormat(email));
                String password = JOptionPane.showInputDialog("Enter password");

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
        
    }//end main
}
