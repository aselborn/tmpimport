package helper;

import dao.ConnectionManager;

import java.sql.*;

public class CreateSQLiteDB {

    public static void createDB (String sqliteDB){

        System.out.println("Skapar databasen " + sqliteDB);
        //String url = "jdbc:sqlite:C:/sqlite/db/" + fileName;
        String url = "jdbc:sqlite:" + sqliteDB;
        try
        {
            Connection conn = ConnectionManager.getSqliteConnected();
            if (conn != null){
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
<<<<<<< HEAD

                conn.close();
=======
>>>>>>> 52fe70d0d7e993e123212095ed6a3c27cf3c8c97
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void createTableData(String sqliteDB) {
        String url = "jdbc:sqlite:" + sqliteDB;
        StringBuilder bu = new StringBuilder();
        bu.append("CREATE TABLE \"Data\" (\n" +
                "\t\"DataId\"\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "\t\"StationId\"\tINTEGER NOT NULL,\n" +
                "\t\"ParameterId\"\tINTEGER NOT NULL,\n" +
                "\t\"Temperature\"\tNUMERIC NOT NULL,\n" +
                "\t\"DateValue\"\tINTEGER,\n" +
                "\t\"TimeValue\"\tINTEGER,\n" +
                "\t\"DateTimeValue\"\tTEXT,\n" +
                "\t\"PeriodId\"\tINTEGER\n" +
                ")\n");
        try{
            Connection conn = ConnectionManager.getSqliteConnected();
            Statement stmt = conn.createStatement();
            stmt.execute(bu.toString());
            System.out.println("Tabellen Data skapad.");
<<<<<<< HEAD
            conn.close();
=======
>>>>>>> 52fe70d0d7e993e123212095ed6a3c27cf3c8c97
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void createTablePeriods(String sqliteDB) {
        String url = "jdbc:sqlite:" + sqliteDB;
        StringBuilder bu = new StringBuilder();
        bu.append("CREATE TABLE \"Periods\" (\n" +
                "\t\"PeriodId\"\tINTEGER NOT NULL,\n" +
                "\t\"PeriodName\"\tTEXT NOT NULL,\n" +
                "\tPRIMARY KEY(\"PeriodId\")\n" +
                ")");
        try{
            Connection conn = ConnectionManager.getSqliteConnected();
            Statement stmt = conn.createStatement();
            stmt.execute(bu.toString());
            System.out.println("Tabellen Periods skapad.");
<<<<<<< HEAD
            conn.close();
=======
>>>>>>> 52fe70d0d7e993e123212095ed6a3c27cf3c8c97
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void createTableRun(String sqliteDB) {
        String url = "jdbc:sqlite:" + sqliteDB;
        StringBuilder bu = new StringBuilder();
        bu.append("CREATE TABLE \"Run\" (\n" +
                "\t\"RunId\"\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "\t\"StationId\"\tINTEGER NOT NULL,\n" +
                "\t\"ParameterId\"\tINTEGER NOT NULL,\n" +
                "\t\"Enabled\"\tINTEGER,\n" +
                "\t\"PeriodId\"\tINTEGER\n" +
                ")");
        try{
            Connection conn = ConnectionManager.getSqliteConnected();
            Statement stmt = conn.createStatement();
            stmt.execute(bu.toString());
            System.out.println("Tabellen Run skapad.");
<<<<<<< HEAD
            conn.close();
=======
>>>>>>> 52fe70d0d7e993e123212095ed6a3c27cf3c8c97
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void createTableRunConfig(String sqliteDB) {
        String url = "jdbc:sqlite:" + sqliteDB;
        StringBuilder bu = new StringBuilder();
        bu.append("CREATE TABLE \"RunConfig\" (\n" +
                "\t\"RunId\"\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "\t\"StationId\"\tINTEGER NOT NULL,\n" +
                "\t\"ParameterId\"\tINTEGER NOT NULL,\n" +
                "\t\"Enabled\"\tINTEGER NOT NULL,\n" +
                "\t\"PeriodId\"\tINTEGER NOT NULL,\n" +
                "\tFOREIGN KEY(\"PeriodId\") REFERENCES \"Periods\"(\"PeriodId\")\n" +
                ")");
        try{
            Connection conn = ConnectionManager.getSqliteConnected();
            Statement stmt = conn.createStatement();
            stmt.execute(bu.toString());
            System.out.println("Tabellen RunConfig skapad.");
<<<<<<< HEAD
            conn.close();
=======
>>>>>>> 52fe70d0d7e993e123212095ed6a3c27cf3c8c97
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void createTableSmhiParameters(String sqliteDB) {
        String url = "jdbc:sqlite:" + sqliteDB;
        StringBuilder bu = new StringBuilder();
        bu.append("CREATE TABLE \"SmhiParameters\" (\n" +
                "\t\"KeyId\"\tINTEGER NOT NULL,\n" +
                "\t\"Title\"\tTEXT NOT NULL,\n" +
                "\t\"Summary\"\tTEXT,\n" +
                "\tPRIMARY KEY(\"KeyId\")\n" +
                ")");
        try{
            Connection conn = ConnectionManager.getSqliteConnected();
            Statement stmt = conn.createStatement();
            stmt.execute(bu.toString());
            System.out.println("Tabellen SmhiParameters skapad.");
<<<<<<< HEAD
            conn.close();
=======
>>>>>>> 52fe70d0d7e993e123212095ed6a3c27cf3c8c97
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void createTableStations(String sqliteDB) {
        String url = "jdbc:sqlite:" + sqliteDB;
        StringBuilder bu = new StringBuilder();
        bu.append("CREATE TABLE \"Stations\" (\n" +
                "\t\"StationId\"\tINTEGER NOT NULL,\n" +
                "\t\"StationName\"\tTEXT NOT NULL,\n" +
                "\t\"Latitud\"\tNUMERIC,\n" +
                "\t\"Longitud\"\tNUMERIC,\n" +
                "\t\"Height\"\tINTEGER,\n" +
                "\t\"FromDateTime\"\tTEXT,\n" +
                "\t\"ToDateTime\"\tTEXT,\n" +
                "\t\"Active\"\tINTEGER NOT NULL,\n" +
                "\tPRIMARY KEY(\"StationId\")\n" +
                ")");
        try{
            Connection conn = ConnectionManager.getSqliteConnected();
            Statement stmt = conn.createStatement();
            stmt.execute(bu.toString());

            System.out.println("Tabellen Stations skapad.");
<<<<<<< HEAD
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

=======

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
>>>>>>> 52fe70d0d7e993e123212095ed6a3c27cf3c8c97
    }

    public static void insertPeriods(String sqliteDB)  {

        String url = "jdbc:sqlite:" + sqliteDB;

        try {
            Connection conn = ConnectionManager.getSqliteConnected();
            StringBuilder sql = new StringBuilder();

            /*
            sql.append("INSERT INTO Periods (PeriodId, PeriodName) VALUES (1, 'latest-hour');");
            sql.append("INSERT INTO Periods (PeriodId, PeriodName) VALUES (2, 'latest-day');");
            sql.append("INSERT INTO Periods (PeriodId, PeriodName) VALUES (3, 'latest-months');");
            sql.append("INSERT INTO Periods (PeriodId, PeriodName) VALUES (4, 'corrected-archive');"); */

            Statement stmt = conn.createStatement();
            stmt.execute("INSERT INTO Periods (PeriodId, PeriodName) VALUES (1, 'latest-hour');");
            stmt.execute("INSERT INTO Periods (PeriodId, PeriodName) VALUES (2, 'latest-day');");
            stmt.execute("INSERT INTO Periods (PeriodId, PeriodName) VALUES (3, 'latest-months');");
            stmt.execute("INSERT INTO Periods (PeriodId, PeriodName) VALUES (4, 'corrected-archive');");

<<<<<<< HEAD
            conn.close();
=======
>>>>>>> 52fe70d0d7e993e123212095ed6a3c27cf3c8c97
        } catch (SQLException e) {
            e.printStackTrace();
        }

<<<<<<< HEAD
=======


>>>>>>> 52fe70d0d7e993e123212095ed6a3c27cf3c8c97
    }
}
