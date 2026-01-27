package src;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class Customer {
    // Database connection properties
    Properties props = new Properties();
    String DB_URL = props.getDbUrl();
    String DB_USER = props.getDbUser();
    String DB_PASSWORD = props.getDbPassword();

    Connection connection = null;
    PreparedStatement pstat = null;

    // Instance variables
    int i = 0;

    // Table columns
    int customerID;
    String firstName;
    String email;
    String phoneNumber;
    String secondName;
    String address;
    String password;

    // Constructor - initializes customer details and inserts into database
    public Customer(String firstName, String secondName, String address, String email, String phoneNumber, String password) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;

        try 
        {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Create SQL insert statement
            String insert = "INSERT INTO customers (CustomerID, FirstName, SecondName, Address, Email, PhoneNumber, Password) VALUES (?, ?, ?, ?, ?, ?, ?)";

            // Establish connection to database
            pstat = connection.prepareStatement(insert);

            // Create prepared statement for updating data in the table
            pstat.setInt(1, customerID);
            pstat.setString(2, firstName);
            pstat.setString(3, secondName);
            pstat.setString(4, address);
            pstat.setString(5, email);
            pstat.setString(6, phoneNumber);
            pstat.setString(7, password);

            // Execute the update
            i = pstat.executeUpdate();
            System.out.println(i + " record successfully updated in the table.");
        } 
        catch (SQLException sqlException) 
        {
            sqlException.printStackTrace();
        }
        // Finally block to close resources
        finally 
        {
            try 
            {
                pstat.close();
                connection.close();
            } 
            catch (SQLException sqlException) 
            {
                sqlException.printStackTrace();
            }
        }
    }  
    
    // Overloaded constructor for removing a customer
    public Customer(String firstName, String secondName,  String password) 
    { 
        this.firstName = firstName;
        this.secondName = secondName;
        this.password = password;

        try 
        {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Create SQL delete statement
            String delete = "DELETE FROM customers WHERE FirstName = ? AND SecondName = ? AND Password = ?";

            // Establish connection to database
            pstat = connection.prepareStatement(delete);

            // Create prepared statement for deleting data in the table
            pstat.setString(1, firstName);
            pstat.setString(2, secondName);
            pstat.setString(3, password);

            // Execute the update
            i = pstat.executeUpdate();
            System.out.println(i + " record successfully deleted from the table.");
        } 
        catch (SQLException sqlException) 
        {
            sqlException.printStackTrace();
        }
        // Finally block to close resources
        finally 
        {
            try 
            {
                pstat.close();
                connection.close();
            } 
            catch (SQLException sqlException) 
            {
                sqlException.printStackTrace();
            }
        }
    }
}