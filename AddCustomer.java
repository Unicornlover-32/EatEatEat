import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class AddCustomer {
    final String DB_URL = "jdbc:mysql://localhost:3306/eateateat";
    Connection connection = null;
    PreparedStatement pstat = null;

    // Static variable to keep track of Customer IDs
    static int IDNo = 1000;
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
    public AddCustomer(String firstName, String secondName, String address, String email, String phoneNumber, String password) {
        this.customerID = IDNo;
        this.firstName = firstName;
        this.secondName = secondName;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        customerID++; // Increment for the next customer

        try {
            connection = DriverManager.getConnection(DB_URL, "root", "Jav@Pr0j$$");
            // Create SQL insert statement
            String sql = "INSERT INTO customers (CustomerID, FirstName, SecondName, Address, Email, PhoneNumber, Password) VALUES (?, ?, ?, ?, ?, ?, ?)";
            // Establish connection to database
            pstat = connection.prepareStatement(sql);

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
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        // Finally block to close resources
        finally {
            try {
                pstat.close();
                connection.close();
            } 
            catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
}   
}