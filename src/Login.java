package src;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

class Login{
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Database connection properties
        Properties props = new Properties();
        String DB_URL = props.getDbUrl();
        String DB_USER = props.getDbUser();
        String DB_PASSWORD = props.getDbPassword();

        Connection connection = null;
        PreparedStatement pstat = null;
        ResultSet resultSet = null;


        System.out.println("Please select an option:");
        System.out.println("1. Create Account \n2. Sign In");
        int choice = scanner.nextInt();

        if (choice == 1) {
            System.out.println("Creating Account...");
            System.out.println("Enter First Name: ");
            String firstName = scanner.next();

            System.out.println("Enter Last Name: ");
            String lastName = scanner.next();

            System.out.println("Enter Address: ");
            String address = scanner.next();
            
            System.out.println("Enter Email: ");
            String email = scanner.next();

            System.out.println("Enter Phone Number: ");
            String phoneNumber = scanner.next();

            System.out.println("Enter Password: ");
            String password = scanner.next();

            Customer customer = new Customer(firstName, lastName, address, email, phoneNumber, password);

        } 
        else if (choice == 2) {
            System.out.println("Signing In...");
            
            System.out.println("Enter Email: ");
            String email = scanner.next();

            System.out.println("Enter Password: ");
            String password = scanner.next();

            try {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

                // Create SQL select statement
                String retrieve = "SELECT * FROM customers WHERE Email = '" + email + "' AND Password = '" + password + "'";

                pstat = connection.prepareStatement(retrieve);
                
                resultSet = pstat.executeQuery();

                
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnsNumber = metaData.getColumnCount();

                System.out.println("You have signed in successfully.");
                while (resultSet.next()) {
                    for (int i = 1; i <= columnsNumber; i++) {
                        if (i > 1) System.out.print(",  ");
                        String columnValue = resultSet.getString(i);
                        System.out.print(metaData.getColumnName(i) + ": " + columnValue);
                    }
                    System.out.println("");
                }

            } 
            catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
            finally {
                try {
                    resultSet.close();
                    pstat.close();
                    connection.close();
                } 
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        } 
        else {
            System.out.println("Invalid choice. Please select 1 or 2.");
        }
    }
}