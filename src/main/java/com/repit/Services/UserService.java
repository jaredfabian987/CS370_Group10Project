package com.repit.Services;

import com.repit.DAOs.UsersDAO;
import com.repit.Model.User;
import org.mindrot.jbcrypt.BCrypt;

/**
 * UserService
 * Handles all user account business logic.
 *
 * Responsibilities:
 * - Registering new users (hashing is handled by UsersDAO)
 * - Authenticating login attempts
 * - General user lookups and deletion
 *
 * Controllers never call UsersDAO directly.
 */
public class UserService {

    private final UsersDAO usersDAO;

    public UserService(UsersDAO usersDAO) {
        this.usersDAO = usersDAO;
    }

    /**
     * Registers a new user account.
     * Called by SetupController when a new user signs up.
     * Password hashing is handled inside UsersDAO.saveUser.
     *
     * @param username    the desired username
     * @param password    the plain-text password (hashed before storage)
     * @param firstName   the user's first name
     * @param lastName    the user's last name
     * @param dateOfBirth the user's date of birth
     * @return true if the account was created successfully, false if the username is taken
     */
    public boolean registerUser(String username, String password,
                                String firstName, String lastName, String dateOfBirth) {
        // userId = -1 because the DB auto-assigns the primary key
        User newUser = new User(-1, username, password, firstName, lastName, dateOfBirth);
        return usersDAO.saveUser(newUser);
    }

    /**
     * Authenticates a login attempt.
     * Called by LoginController to verify credentials.
     * Returns the full User object on success so the controller can store the userId for the session.
     *
     * @param username the username submitted at login
     * @param password the plain-text password to check
     * @return the User object if credentials match, null if authentication fails
     */
    public User loginUser(String username, String password) {
        User user = usersDAO.getUser(username);
        if (user == null) return null;

        // BCrypt.checkpw compares the plain-text password against the stored hash
        boolean matches = BCrypt.checkpw(password, user.getPassword());
        return matches ? user : null;
    }

    /**
     * Looks up a user by their ID.
     */
    public User getUser(int userId) {
        return usersDAO.getUser(userId);
    }

    /**
     * Looks up a user by their username.
     */
    public User getUser(String username) {
        return usersDAO.getUser(username);
    }

    /**
     * Deletes a user account.
     */
    public void deleteUser(int userId) {
        usersDAO.deleteUser(userId);
    }

    /**
     * Changes the user's password.
     * TODO: implement once password-change screen is built
     */
    public void changePassword() {

    }
}
