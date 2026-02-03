package src;

import javax.swing.JOptionPane; 
import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

class Login1{
    public Login1(String email, String password) 
    {
        // Database connection properties
        Properties props = new Properties();
        String DB_URL = props.getDbUrl();
        String DB_USER = props.getDbUser();
        String DB_PASSWORD = props.getDbPassword();

        Connection connection = null;
        PreparedStatement pstat = null;
        ResultSet resultSet = null;

        Boolean loggedIn = false;
        int customerID = -1;

        try 
        {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Create SQL select statement
            String retrieve = "SELECT * FROM customers WHERE Email = '" + email + "' AND Password = '" + password + "'";

            pstat = connection.prepareStatement(retrieve);
            
            resultSet = pstat.executeQuery();
            
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnsNumber = metaData.getColumnCount();

            if (resultSet.next()) 
            {
                loggedIn = true;
                customerID = resultSet.getInt("customerID");
            } 
            else 
            {
                JOptionPane.showMessageDialog(null, "Login failed. Please check your email and password.");
            } 
        }
        catch (SQLException sqlException) 
        {
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

        if (loggedIn) 
        {
            Account account = new Account(customerID);
        }
    }
}