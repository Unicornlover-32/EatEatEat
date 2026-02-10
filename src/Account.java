package src;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

// Account class to handle account-related operations
// This will show account details and settings for logged-in customer
// Currently in developement for changing customer details
public class Account 
{

    public Account(int customerID) 
    {
        Scanner scanner = new Scanner(System.in);

        // Database connection properties
        Properties props = new Properties();
        String DB_URL = props.getDbUrl();
        String DB_USER = props.getDbUser(); 
        String DB_PASSWORD = props.getDbPassword();

        Connection connection = null;
        PreparedStatement pstat = null;

        try
        {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        
            String firstName = JOptionPane.showInputDialog(null, "Enter a new first name for customer: " + customerID);
         
            String update = "UPDATE customers SET firstName = ? WHERE customerID = ?";

            pstat = connection.prepareStatement(update);
            pstat.setString(1, firstName);
            pstat.setInt(2, customerID);

        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        finally 
        {
            try 
            {
                pstat.close();
                connection.close();
            } 
            catch (Exception exception) 
            {
                exception.printStackTrace();
            }
        }
        
    }
}
