package com.repit.DAOs;
import com.repit.Model.Exercise;
import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;

public class BaseDAO {
    private static final String url = "jdbc:sqlite:repit.db";

    // One shared connection for all DAOs — SQLite is single-file and our app is
    // single-threaded (JavaFX Application Thread), so one connection is enough.
    // Multiple connections to the same SQLite file cause SQLITE_BUSY lock errors.
    private static Connection sharedConnection;

    protected Connection connection;

    public BaseDAO() {
        if (sharedConnection == null) {
            try {
                sharedConnection = DriverManager.getConnection(url);
                // WAL mode is set once on the shared connection
                Statement st = sharedConnection.createStatement();
                st.execute("PRAGMA journal_mode=WAL;");
                st.close();
            } catch (SQLException e) {
                System.out.println("BaseDAO connection error: " + e.getMessage());
            }
        }
        connection = sharedConnection;
    }
}
