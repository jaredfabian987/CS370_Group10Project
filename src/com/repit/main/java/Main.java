package com.repit.main.java;

import java.sql.*;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        connect();
    }

    public static void connect(){
        String url = "jdbc:sqlite:repit.db";

        try (Connection conn = DriverManager.getConnection(url)){
            if (conn != null){
                System.out.println("Connected to the database");
            }
            }
        }
}