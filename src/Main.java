import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

void main() {


    // JUST FOR TESTING!!
    try{
        Connection conn1 = DriverManager.getConnection("jdbc:sqlite:repit.db");
        Statement stmt1 = conn1.createStatement();

        stmt1.execute("CREATE TABLE IF NOT EXISTS Users (" +
                "id INTEGER PRIMARY KEY," +
                "name TEXT NOT NULL," +
                "userId TEXT NOT NULL UNIQUE" +
                ")"
        );

        stmt1.execute("INSERT INTO Users (name, userId) " +
                "VALUES ('Myrhen', 69420)");


    } catch (Exception e) {
        if (e instanceof SQLException se){
            if (se.getErrorCode() == 19 && se.getMessage().contains("UNIQUE constraint failed")) {
                System.out.println("CANNOT CREATE RECORD!");
            } else{
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("Error: " + e.getMessage());
        }

    }
}