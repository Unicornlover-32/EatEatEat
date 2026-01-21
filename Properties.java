// Properties.java
// This class contains the database connection properties.
// It provides methods to access the database URL, user, and password.
// Note: In a production environment, sensitive information like database credentials
// should be stored securely and not hard-coded.

public class Properties {
    final String DB_URL = "jdbc:mysql://localhost:3306/eateateat";
    final String DB_USER = "root";
    final String DB_PASSWORD = "Jav@Pr0j$$";

    public String getDbUrl() {
        return DB_URL;
    }

    public String getDbUser() {
        return DB_USER;
    }

    public String getDbPassword() {
        return DB_PASSWORD;
    }
}
