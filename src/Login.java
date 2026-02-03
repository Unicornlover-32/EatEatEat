package src;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

class Login{
    public static void main(String[] args) 
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

        String firstName = "";
        String lastName = "";
        String address = "";
        String email = "";
        String phoneNumber = "";
        String password = "";
        String confirmPassword = "";
        Boolean loggedIn = false;

        System.out.println("Please select an option:");
        System.out.println("1. Create Account \n2. Sign In");
        int choice = scanner.nextInt();

        if (choice == 1) 
        {
            System.out.println("Creating Account...");
            System.out.println("Enter First Name: ");
            firstName = scanner.next();

            System.out.println("Enter Last Name: ");
            lastName = scanner.next();

            System.out.println("Enter Address: ");
            address = scanner.next();
            
            System.out.println("Enter Email: ");
            email = scanner.next();

            System.out.println("Enter Phone Number: ");
            phoneNumber = scanner.next();

            System.out.println("Enter Password: ");
            password = scanner.next();

            System.out.println("Enter Password: ");
            confirmPassword = scanner.next();

            if (password.equals(confirmPassword)) 
            {
                Customer customer = new Customer(firstName, lastName, address, email, phoneNumber, password);
                loggedIn = true;
            } 
            else 
            {
                System.out.println("Passwords do not match.");
            }
        } 
        else if (choice == 2) 
        {
            System.out.println("Signing In...");
            
            System.out.println("Enter Email: ");
            email = scanner.next();

            System.out.println("Enter Password: ");
            password = scanner.next();

            try 
            {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

                // Create SQL select statement
                String retrieve = "SELECT * FROM customers WHERE Email = '" + email + "' AND Password = '" + password + "'";

                pstat = connection.prepareStatement(retrieve);
                
                resultSet = pstat.executeQuery();

                
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnsNumber = metaData.getColumnCount();

                System.out.println("You have signed in successfully.");

                loggedIn = true;
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
        } 
        else 
        {
            System.out.println("Invalid choice. Please select 1 or 2.");
        }

        if (loggedIn) 
        {
            System.out.println("Welcome to your account!");
            
            try 
            {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

                // Create SQL select statement to get customerID
                String retrieveID = "SELECT customerID FROM customers WHERE Email = '" + email + "' AND Password = '" + password + "'";

                pstat = connection.prepareStatement(retrieveID);
                
                resultSet = pstat.executeQuery();

                int customerID = -1;
                if (resultSet.next()) 
                {
                    customerID = resultSet.getInt("customerID");
                }

                Account account = new Account(customerID);
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
        }
    }
}