package dao;

import helper.Util;
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

            String url = "jdbc:mysql://localhost/temperature?";
            //String url = "jdbc:mysql://192.168.86.42/temperature?";
            String user = "root";
            //String user = "remote";
            String pwd = "lytill53";
            //String pwd = "lytill53ZYX";

            //Class.forName("com.mysql.jdbc.Driver");
            Class.forName("com.mysql.cj.jdbc.Driver");
            DriverManager.getDriver(url);

            myConnection = DriverManager.getConnection(url, user, pwd);
            return myConnection;
            //return DriverManager.getConnection("jdbc:mysql://localhost/temperature?user=root&password=lytill53");

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
            String dbName = Util.readConfiguration("dbname");
            return DriverManager.getConnection("jdbc:sqlite:" + dbName);

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