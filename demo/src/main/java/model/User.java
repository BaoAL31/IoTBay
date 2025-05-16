package model;

import java.util.Random;

public class User {
    private int userID;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;

    // Constructors
    public User() {}

    public User(String name, String email, String password, String phoneNumber, String address) {
        this.userID = generateRandomUserID();
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    private int generateRandomUserID() {
        Random rand = new Random();
        return 10000 + rand.nextInt(90000);
    }

    // Getters and setters
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
