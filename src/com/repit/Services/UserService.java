package com.repit.Services;

import com.repit.DAOs.UsersDAO;
import com.repit.Model.User;

public class UserService {
    private final UsersDAO usersDAO = new UsersDAO();

    public void addUser(int userId, String username, String password, String firstName, String lastName, String date_of_birth){
        User newUser = new User(userId, username, password, firstName, lastName, date_of_birth);
        boolean isSuccessful = usersDAO.saveUser(newUser);
        if (isSuccessful) {
            System.out.println("User successfully added!");
        } else{
            System.out.println("There was an error saving user!");
        }
    }

    public User getUser(String username){
        return usersDAO.getUser(username);
    }

    public User getUser(int userId){
        return usersDAO.getUser(userId);
    }

    public void deleteUser(int userId){
        usersDAO.deleteUser(userId);
    }

    public void changePassword(){

    }
}
