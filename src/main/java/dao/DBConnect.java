package dao;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public abstract class DBConnect {

    private static Connection mConnection;
    private static Connection mMySqlConnection;

    public static Connection getConnection() throws ClassNotFoundException, SQLException {

        if (mConnection ==null){
            Class.forName("org.postgresql.Driver");
            Properties properties=new Properties();

            //hardcoded stuff
            properties.setProperty("user", "root");
            properties.setProperty("password", "lytill53");
            properties.setProperty("db", "trackme");
            properties.setProperty("host", "localhost");
            properties.setProperty("port", "5434");

            mConnection = DriverManager.getConnection(buildConnectionString(properties));
        }

        return mConnection;

    }

    public static Connection getMySqlConnection() throws ClassNotFoundException, SQLException{

        if (mMySqlConnection == null){

            String mysqlUrl = "jdbc:mysql://localhost:3306/temperature";
            String user = "root";
            String pwd = "lytill53";
            Connection cn = null;

            try{
                mMySqlConnection = DriverManager.getConnection(mysqlUrl, user, pwd);
                return mMySqlConnection;
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }
        return null;
    }

    /*
    private static String buildConnectionString(Properties prop) {
        return String.format("jdbc:postgresql://%s:%d/%s?user=%s&password=%s", prop.getProperty("host"), Integer.parseInt(prop.getProperty("port")), prop.getProperty("db"),
                prop.getProperty("user"), prop.getProperty("password"));
    }

    */

    private static String buildConnectionString(Properties prop){
        return String.format("jdbc:mysql://localhost/Temperature?user=%s&password=%s", prop.getProperty("user"), prop.getProperty("password"));
    }


}

