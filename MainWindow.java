import javax.swing.JOptionPane; // program uses JOptionPane

public class MainWindow {
    // Addition program


    public static void main( String [] args ){
        // obtain user input
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
    }//end main
}
