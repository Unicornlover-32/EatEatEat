package src;

// Basket class
// This creates the basket which can confirm the order
// Also use as an object class to store the basket items
public class Basket
{
    // information passed from the HomeFrame
    // will be used to store as variables to be used in the basket panel
    private int customerID;
    private int restaurantID;
    private int itemID;
    private int quantity;
    private String itemName;
    private double price;

    public Basket(int customerID, int restaurantID, int itemID ,int quantity, String itemName, double price)
    {
        this.customerID = customerID;
        this.restaurantID = restaurantID;
        this.itemID = itemID;
        this.quantity = quantity;
        this.itemName = itemName;
        this.price = price;
    }

     // Getters
    public int getQuantity()
    {
        return quantity;
    }

    public int getItemID()
    {
        return itemID;
    }

    public String getItemName()
    {
        return itemName;
    }

    public double getPrice()
    {
        return price;
    }

    // Setters
    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public void setItemID(int itemID)
    {
        this.itemID = itemID;
    }

    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }

}
