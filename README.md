EatEatEat - Food Delivery Application
EatEatEat is a Java-based desktop application designed for a food delivery service. It allows users to browse restaurant menus, manage a shopping basket, place orders, and view their order history through a clean and consistent graphical user interface.

Features
  • User Authentication: Secure login and account creation using BCrypt password hashing.
  • Restaurant Browsing: View a list of available restaurants and their specific menus.
  • Order Management: Add items to a basket, place orders, and receive order confirmation.
  • Order History: View previous orders and detailed item breakdowns for each order.
  • Profile Management: View and manage user account details.
  • Modern UI: Built with Swing and MigLayout for a responsive and standardized visual experience.
  
Prerequisites
  • Java JDK: 11 or higher.
  • MySQL Database: To store user, restaurant, and order data.
  • Maven: For dependency management and building the project.
Database Setup

  1. Create a MySQL database named eateateat.
  2. Configure your database credentials in src/main/java/Properties.java:
    ◦ DB_URL: jdbc:mysql://localhost:3306/eateateat
    ◦ DB_USER: Your MySQL username (default: root).
    ◦ DB_PASSWORD: Your MySQL password.
