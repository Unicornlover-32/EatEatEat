package src;

import javax.swing.JOptionPane; // program uses JOptionPane

public class MainWindow {

    public static void main( String [] args ){
        // prompt user for input
        // Menu for user options
        int option = JOptionPane.showOptionDialog(null, "Choose an option:", "Main Menu", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Add Customer", "Remove Customers"}, null);

        if (option == 0) {
            // Gather customer details
            String name = JOptionPane.showInputDialog("Enter your name" );
            String surname = JOptionPane.showInputDialog("Enter your surname");
            String address = JOptionPane.showInputDialog("Enter address");
            String email = JOptionPane.showInputDialog("Enter email");
            String phoneNumber = JOptionPane.showInputDialog("Enter phone number");
            String password = JOptionPane.showInputDialog("Enter password");    
            
            //Add customer to the database
            AddCustomer customer = new AddCustomer(name, surname, address, email, phoneNumber, password);
            
            // display result
            JOptionPane.showMessageDialog(null, "Name: " + name + " " + surname +
                                    "\nAddress: " + address +
                                    "\nEmail: " + email +
                                    "\nPhone Number: " + phoneNumber, "Customer Details", JOptionPane.PLAIN_MESSAGE );
        } else if (option == 1) {
            // Gather customer details
            String name = JOptionPane.showInputDialog("Enter your name" );
            String surname = JOptionPane.showInputDialog("Enter your surname");
            String password = JOptionPane.showInputDialog("Enter password");    
            
            //Add customer to the database
            AddCustomer customer = new AddCustomer(name, surname, password);
            
            // display result
            JOptionPane.showMessageDialog(null, "Customer has been removed.\nName: " + name + " " + surname, "Customer Details", JOptionPane.PLAIN_MESSAGE );
        } else {
            JOptionPane.showMessageDialog(null, "Invalid option selected.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }//end main
}
