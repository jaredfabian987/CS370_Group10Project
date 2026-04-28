package com.repit.DAOs;
import com.repit.Model.User;
import com.repit.Model.WorkoutLog;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

import javax.xml.transform.Result;

public class UsersDAO extends BaseDAO {
    private static final String SELECT_USERNAME_SQL =
            "SELECT * FROM users WHERE username = ?";

    private static final String SELECT_USERID_SQL =
            "SELECT * FROM users WHERE userId = ?";

    private static final String TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS users (" +
                    "userId INTEGER PRIMARY KEY," +
                    "username INTEGER UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "firstName TEXT NOT NULL,"+
                    "lastName TEXT NOT NULL," +
                    "date_of_birth TEXT NOT NULL"+
                    ")";

    private static final String INSERT_SQL =
            "INSERT INTO users (username, password, firstName, lastName, date_of_birth) "+
                    "VALUES (?,?,?,?,?)";

    private static final String DELETE_SQL =
            "DELETE FROM users WHERE userId = ?";

    private static final String UPDATE_SQL =
            "UPDATE users SET password = ? WHERE username = ?";


    //Needs encryption or some sort of security measures
    public User getUser(int userId){
        try{
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = connection.prepareStatement(SELECT_USERID_SQL);
            pstmt.setInt(1, userId);
            //pstmt.executeUpdate();

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                return new User(
                        rs.getInt("userId"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("date_of_birth")
                );
            } else {
                System.out.println("User NOT FOUND!");
                return null;
            }
        } catch (Exception e){
            System.out.println("Users Database Error: "+ e.getMessage());
            return null;
        }
    }

    public User getUser(String username){
        try{
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = connection.prepareStatement(SELECT_USERNAME_SQL);
            pstmt.setString(1, username);
            //pstmt.executeUpdate();

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                return new User(
                        rs.getInt("userId"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("date_of_birth")
                );
            } else {
                System.out.println("User NOT FOUND!");
                return null;
            }
        } catch (Exception e){
            System.out.println("Users Database Error: "+ e.getMessage());
            return null;
        }
    }

    public boolean verifyUser(User user){
        try{
            PreparedStatement pstmt;
            if(user.getUserId() == -1){
                //UserID exists look for userId
                pstmt = connection.prepareStatement(SELECT_USERID_SQL);
                pstmt.setInt(1, user.getUserId());
            }else{
                //Search for Username instead
                pstmt = connection.prepareStatement(SELECT_USERNAME_SQL);
                pstmt.setString(1, user.getUsername());
            }
            //pstmt.executeUpdate();

            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()){ // only 1 user data should be found
                return false;
            }

            boolean matches = BCrypt.checkpw(user.getPassword(), rs.getString("password"));
            if (matches){
                return true;
            }
        } catch(Exception e){
            System.out.println("Verification Error: "+e.getMessage());
        }
        return false;
    }

    public boolean saveUser(User pUser){
        try{
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = connection.prepareStatement(INSERT_SQL);
            String cryptedPassword = BCrypt.hashpw(pUser.getPassword(), BCrypt.gensalt());
            pstmt.setString(1, pUser.getUsername());
            pstmt.setString(2, cryptedPassword);
            pstmt.setString(3, pUser.getFirstName());
            pstmt.setString(4, pUser.getLastName());
            pstmt.setString(5, pUser.getDate_of_birth());
            pstmt.executeUpdate();
            return true;
        }catch(Exception e) {
            System.out.println("Users Database Error: "+ e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(int userId){
        try{
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = connection.prepareStatement(DELETE_SQL);
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            return true;
        } catch(Exception e) {
            System.out.println("Users Database Error: "+ e.getMessage());
            return false;
        }
    }
}
