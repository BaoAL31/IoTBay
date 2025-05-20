package model;

// Model class for devices/products in IoTBay
public class Device {
    private int id;           // database primary key
    private String name;      // device name 
    private String type;      // device category (e.g., “Sensor”)
    private double price;     // unit price
    private int stock;        // stock quantity

    // Constructor for all fields
    public Device(int id, String name, String type, double price, int stock) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.stock = stock;
    }

    // getters and setters

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }

    // override toString to help with debugging
    @Override
    public String toString() {
        return "Device{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", type='" + type + '\'' +
               ", price=" + price +
               ", stock=" + stock +
               '}';
    }
}
