package dao;

import helper.TemperatureCSV;
import helper.TemperatureObject;
import helper.Util;
import model.TemperaturData;
import model.TemperaturModel;

import java.sql.*;
import java.util.List;

public class Inserter  {


    private  List<SmhiParameters> m_SmhiParameters;
    private TemperatureObject m_TemperatureObject;

    private static String CONTAINS_MONTH = "månad";
    private boolean useSQLite = Boolean.parseBoolean(Util.readConfiguration("usesqlite"));
    private List<Stations> m_stationList;


    public Inserter() {}

    public void setTemperatureObject(TemperatureObject temperatureObject){
        m_TemperatureObject=temperatureObject;
    }


    public int insertStations() throws  SQLException{
        int rowsInserted = -1;
        Connection thisConnection = useSQLite ? ConnectionManager.getSqliteConnected() : ConnectionManager.getConnected();
        thisConnection.setAutoCommit(false);
        int batchSize=100;
        int insertCount = 0;



        String sqlInsert = "INSERT INTO Stations(StationId, StationName, Latitud, Longitud, Height, FromDateTime, ToDateTime, Active) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = thisConnection.prepareStatement(sqlInsert);

        try{
            for( Stations prm : m_stationList){

                pstmt.setInt(1, prm.getStationId());
                pstmt.setString(2, prm.getStationName());
                pstmt.setDouble(3, prm.getLatitud());
                pstmt.setDouble(4, prm.getLongitud());
                pstmt.setDouble(5, prm.getHeight());
                pstmt.setLong(6, prm.getFromDateTime());
                pstmt.setLong(7, prm.getToDateTime());
                pstmt.setInt(8, prm.getActive());

                pstmt.addBatch();

                if (++insertCount % batchSize == 0){
                    pstmt.executeBatch();
                }

            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        int[] n = pstmt.executeBatch();
        thisConnection.commit();

        rowsInserted=insertCount;

        return rowsInserted;

    }

    public int insertSmhiParameters() throws SQLException {
        int rowsInserted = -1;

        Connection thisConnection = useSQLite ? ConnectionManager.getSqliteConnected() : ConnectionManager.getConnected();
        thisConnection.setAutoCommit(false);

        int batchSize=100;
        int insertCount = 0;

        String sqlInsert = "INSERT INTO SmhiParameters(Key, Title, Summary) VALUES(?, ?, ?)";
        PreparedStatement pstmt = thisConnection.prepareStatement(sqlInsert);

        try{
            for( SmhiParameters prm : m_SmhiParameters){

                pstmt.setInt(1, prm.getKey());
                pstmt.setString(2, prm.getTitle());
                pstmt.setString(3, prm.getSummary());

                pstmt.addBatch();

                if (++insertCount % batchSize == 0){
                    pstmt.executeBatch();
                }

            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        int[] n = pstmt.executeBatch();
        thisConnection.commit();

        rowsInserted=insertCount;

        return rowsInserted;

    }

    public int insertData(){

        int rowsInserted = -1;
        int newKeyInserted = -1;
        if (m_TemperatureObject == null || m_TemperatureObject.getTemperatureCSVList().size() == 0){
            return  0;
        }

        int newLocationId = dbSaveLocation();

        //What kind of data ...
        if (m_TemperatureObject.isMonthAvarage()) {
            //Save Month.
            rowsInserted = saveMonthData(newLocationId);
        } else{

            rowsInserted = insertTemperature(newLocationId);
        }


        return rowsInserted;
    }


    private int saveMonthData(int locationId) {

        int rowsInserted = -1;

        String sqlInsert="INSERT INTO MonthData (LocationId, RepYear, RepMonth, TempData) VALUES(?,?,?,?)";

        //Connection thisConnection = ConnectionManager.getConnected();
        Connection thisConnection = useSQLite ? ConnectionManager.getSqliteConnected() : ConnectionManager.getConnected();

        try{

            thisConnection.setAutoCommit(false);

            PreparedStatement pstmt = thisConnection.prepareStatement(sqlInsert);

            int insertCount=0;
            int batchSize=100;

            for (TemperatureCSV temperatureCSV : m_TemperatureObject.getTemperatureCSVList()){

                pstmt.setInt(1, locationId);
                pstmt.setInt(2, temperatureCSV.getRepYear());
                pstmt.setInt(3, temperatureCSV.getRepMonth());

                pstmt.setDouble(4, Double.parseDouble(temperatureCSV.getTempString()));

                pstmt.addBatch();

                if (++insertCount % batchSize == 0){
                    pstmt.executeBatch();
                }

            }

            int[] n = pstmt.executeBatch();
            thisConnection.commit();

            rowsInserted=insertCount;


        } catch (SQLException e) {
            e.printStackTrace();
        }


        return rowsInserted;
    }


    private int dbSaveLocation() {


        String sql = useSQLite ? "INSERT INTO Location(LocationName, LocationNumber, " +
                "LocationHeight, LocationStart, LocationStop, LocationLatitude, LocationLongitude, LocationTemperatureType) VALUES(?,?,?,?,?,?,?,?)" :

                "INSERT INTO Temperature.Location(LocationName, LocationNumber, " +
                        "LocationHeight, LocationStart, LocationStop, LocationLatitude, LocationLongitude, LocationTemperatureType) VALUES(?,?,?,?,?,?,?,?)";

        Statement stmt = null;

        try{

            int keyInserted=0;

            //Connection thisConnection = useSQLite ? ConnectionManager.getSqliteConnected() : ConnectionManager.getConnected();
            PreparedStatement pstmt = useSQLite ? ConnectionManager.getSqliteConnected().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS) :
                    ConnectionManager.getConnected().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, m_TemperatureObject.getStationsNamn());
            pstmt.setString(2, m_TemperatureObject.getKlimatNummer());
            pstmt.setDouble(3, m_TemperatureObject.getHeight());
            pstmt.setTimestamp(4,Timestamp.valueOf(m_TemperatureObject.getLocationStart()));
            pstmt.setTimestamp(5,Timestamp.valueOf(m_TemperatureObject.getLocationStop()));

            pstmt.setDouble(6, m_TemperatureObject.getLatitude());
            pstmt.setDouble(7, m_TemperatureObject.getLongitude());
            pstmt.setString(8, m_TemperatureObject.getBeskrivning());

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()){
                keyInserted=rs.getInt(1);
            }


            return keyInserted;



        } catch (SQLException e) {
            e.printStackTrace();
        }

        return  -1;

    }

    private int insertTemperature (int locationId){

        int rowsInserted = 0;
        int batchSize=100;

        String sqlInsert = "INSERT INTO Tempdata(LocationId, TempDate, Tempdata) VALUES (?,?,?)";

        try {

            Connection thisConnection = useSQLite ? ConnectionManager.getSqliteConnected() : ConnectionManager.getConnected();
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

            rowsInserted=insertCount;


        } catch (SQLException e) {
            e.printStackTrace();
        }


        return rowsInserted;
    }


    public void setSmhiParameters(List<SmhiParameters> smhiParameters) {
        m_SmhiParameters=smhiParameters;
    }

    public void setStationList(List<Stations> stationsList) {
        m_stationList = stationsList;
    }

    /*
        Save some data to table Data
     */
    public void save(TemperaturModel temperaturModel, Integer stationId, Integer parameterId, Integer periodId) throws SQLException {

        Connection thisConnection = useSQLite ? ConnectionManager.getSqliteConnected() : ConnectionManager.getConnected();

        String sqlTruncate ="DELETE from data WHERE StationId = ? AND PeriodId = ?";
        PreparedStatement statement = thisConnection.prepareStatement(sqlTruncate);
        statement.setInt(1, stationId);
        statement.setInt(2, periodId);

        int row = statement.executeUpdate();
        System.out.println("Deleted ".concat(String.valueOf(row)).concat( " lines"));

        String sql = "INSERT INTO Data(StationId, ParameterId, Temperature, DateValue, TimeValue, DateTimeValue, PeriodId)";
        sql += " VALUES (?, ?, ?, ?, ?, ?, ?)";

        thisConnection.setAutoCommit(false);
        PreparedStatement pstmt = thisConnection.prepareStatement(sql);

        int insertCount=0;
        int batchSize=100;

        for(TemperaturData data : temperaturModel.getmData()){

            String dateTime = data.getDatum().concat(" ").concat(data.getKlockslag());

            pstmt.setInt(1, stationId);
            pstmt.setInt(2, parameterId);
            pstmt.setDouble(3, data.getTemperatur());
            pstmt.setString(4, data.getDatum());
            pstmt.setString(5, data.getKlockslag());
            pstmt.setString(6, dateTime);
            pstmt.setInt(7, periodId);
            pstmt.addBatch();

            if (++insertCount % batchSize == 0){
                pstmt.executeBatch();
            }
        }

        int[] n = pstmt.executeBatch();
        thisConnection.commit();


    }
}
