package dao;

import helper.TemperatureCSV;
import helper.TemperatureObject;
import helper.Util;

import java.sql.*;
import java.util.ArrayList;

public class Inserter  {

    private TemperatureObject m_TemperatureObject;

    public Inserter(TemperatureObject temperatureObject){
        m_TemperatureObject=temperatureObject;
    }

    public int insertData(){

        int rowsInserted = -1;

        if (m_TemperatureObject == null || m_TemperatureObject.getTemperatureCSVList().size() == 0){
            return  0;
        }

        String sql = "INSERT INTO Temperature.Location(LocationName, LocationNumber, LocationHeight, LocationStart, LocationStop, LocationLatitude, LocationLongitude) VALUES(?,?,?,?,?,?,?)";
        Statement stmt = null;


        try{

            int keyInserted=0;

            PreparedStatement pstmt = ConnectionManager.getConnected().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, m_TemperatureObject.getStationsNamn());
            pstmt.setString(2, m_TemperatureObject.getKlimatNummer());
            pstmt.setDouble(3, m_TemperatureObject.getHeight());
            pstmt.setTimestamp(4,Timestamp.valueOf(m_TemperatureObject.getLocationStart()));
            pstmt.setTimestamp(5,Timestamp.valueOf(m_TemperatureObject.getLocationStop()));

            pstmt.setDouble(6, m_TemperatureObject.getLatitude());
            pstmt.setDouble(7, m_TemperatureObject.getLongitude());

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()){
                keyInserted=rs.getInt(1);
            }


            if (keyInserted != 0){
                rowsInserted = insertTemperature(keyInserted);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


        return rowsInserted;
    }

    private int insertTemperature (int locationId){

        int rowsInserted = 0;
        int batchSize=100;

        String sqlInsert = "INSERT INTO Tempdata(LocationId, TempDate, Tempdata) VALUES (?,?,?)";

        try {

            Connection thisConnection = ConnectionManager.getConnected();
            thisConnection.setAutoCommit(false);

            PreparedStatement pstmt = thisConnection.prepareStatement(sqlInsert);

            int insertCount=0;

            for (TemperatureCSV temperatureCSV : m_TemperatureObject.getTemperatureCSVList()){

                pstmt.setInt(1, locationId);
                pstmt.setTimestamp(2, Timestamp.valueOf( Util.getDateTime(temperatureCSV.getDatString().concat(" ").concat(temperatureCSV.getTimeString()))));
                pstmt.setDouble(3, Double.parseDouble(temperatureCSV.getTempString()));

                pstmt.addBatch();

                if (++insertCount % batchSize == 0){
                    pstmt.executeBatch();
                }

            }

            int[] n = pstmt.executeBatch();
            thisConnection.commit();


        } catch (SQLException e) {
            e.printStackTrace();
        }


        return rowsInserted;
    }


}
