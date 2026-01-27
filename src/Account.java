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
// Currently a placeholder for future implementation
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
        ResultSet resultSet = null;

        try
        {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Create SQL select statement
            String select = "SELECT * FROM customers WHERE customerID = '" + customerID + "'"; // Placeholder customerID

            pstat = connection.prepareStatement(select);
            resultSet = pstat.executeQuery();

            resultSet.next();
            JOptionPane.showMessageDialog(null, "First Name: " + resultSet.getString("FirstName") + 
                                                                "\nSecond Name: " + resultSet.getString("SecondName") +
                                                                "\nAddress: " + resultSet.getString("Address") +
                                                                "\nEmail: " + resultSet.getString("Email") +
                                                                "\nPhone Number: " + resultSet.getString("PhoneNumber")
                                                                , "Account Details", JOptionPane.PLAIN_MESSAGE);
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        finally 
        {
            try 
            {
                resultSet.close();
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
