package dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    private static final Logger log=LoggerFactory.getLogger(ConnectionManager.class);
    private static Connection myConnection = null;
    private static Connection mySqliteConnection=null;

    public static Connection getConnected () {

        try {

            if (myConnection != null){
                return myConnection;
            }

            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost/Temperature?user=root&password=lytill53");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    public static Connection getSqliteConnected(){

        if (mySqliteConnection != null){
            return mySqliteConnection;
        }

        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:db/temperature.db");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            log.error(e.toString());
        } catch (SQLException e) {
            log.error(e.toString());
            log.error(e.getSQLState());
        }

        return null;
    }

}