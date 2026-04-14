package com.repit.DAOs;
import com.repit.Model.Exercise;
import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;

public class BaseDAO {
    private static final String url = "jdbc:sqlite:repit.db";
    protected Connection connection;
    public BaseDAO(){
        try{
            connection = DriverManager.getConnection(url);
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
