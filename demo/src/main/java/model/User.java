package model;

// Model class for user accounts in IoTBay
public class User {
    private int userID;          // database primary key
    private String fullName;     // user’s full name
    private String email;        // user’s email (used to log in)
    private String password;     // user’s password (should be hashed)
    private String phoneNumber;  // contact number
    private String address;      // mailing address
    private String userType;     // role: "user" or "admin"

    // Default constructor
    public User() {}

    // Constructor without ID (ID set when saved to DB)
    public User(String fullName, String email, String password,
                String phoneNumber, String address, String userType) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.userType = userType;
    }

    // getters and setters

    public int getUserID() {
        return userID;
    }
    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getUserType() {
        return userType;
    }
    public void setUserType(String userType) {
        this.userType = userType;
    }
}
