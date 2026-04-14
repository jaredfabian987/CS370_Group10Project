package com.repit.Model;

public class User {
    private int userId = -1;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String date_of_birth;
    private boolean isVerified = false;

    //Usage: setting and saving
    public User(String username, String password, String firstName, String lastName, String date_of_birth) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.date_of_birth = date_of_birth;
    }

    //Usage: data retrieval
    public User(int userId, String username, String password, String firstName, String lastName, String date_of_birth) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.date_of_birth = date_of_birth;
    }

    public int getUserId(){ return userId;}
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getDate_of_birth() {return date_of_birth; }
    public void verifyUser(boolean isVerified){ this.isVerified = isVerified; }
}
