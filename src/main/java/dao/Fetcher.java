package dao;

import helper.CsvScanner;
import helper.RunConfiguration;
import helper.Util;
import model.SmhiPeriods;
import model.Stations;
import smhi.JSONParse;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Fetcher {

    private final boolean useSQLite = Boolean.parseBoolean(Util.readConfiguration("usesqlite"));

    public Integer TableCount(String table){
        Integer count = 0;

        String select =  "SELECT COUNT(*) FROM ".concat(table);
        Connection thisConnection = useSQLite ? ConnectionManager.getSqliteConnected() : ConnectionManager.getConnected();
        try {

            //PreparedStatement pstmt = thisConnection.prepareStatement(select);
            ResultSet rs = thisConnection.createStatement().executeQuery(select);

            while (rs.next()){
                count =rs.getInt(1);
            }



        } catch(Exception e){
            e.printStackTrace();
        }

        return count;
    }


    public Integer SmhiParameterCount(){

        Integer count = 0;

        String select = "SELECT COUNT(*) FROM SmhiParameters";
        Connection thisConnection = useSQLite ? ConnectionManager.getSqliteConnected() : ConnectionManager.getConnected();
        try {

            //PreparedStatement pstmt = thisConnection.prepareStatement(select);
            ResultSet rs = thisConnection.createStatement().executeQuery(select);

            while (rs.next()){
                count =rs.getInt(1);
            }



        } catch(Exception e){
            e.printStackTrace();
        }

        return count;
    }


    public int StationCount() {
        return 0;
    }

    public List<RunConfiguration> getRunconfigList() {
        List<RunConfiguration> configs = new ArrayList<>();
        String select =  "SELECT r.StationId, ParameterId, PeriodName, enabled, s.StationName, p.PeriodId FROM RunConfig r" +
                " INNER JOIN Stations s on r.StationId = s.StationId " +
                " INNER JOIN Periods p ON p.PeriodId = r.PeriodId";

        Connection thisConnection = useSQLite ? ConnectionManager.getSqliteConnected() : ConnectionManager.getConnected();

        try{
            ResultSet rs = thisConnection.createStatement().executeQuery(select);

            while (rs.next()){
                RunConfiguration conf = new RunConfiguration();

                conf.setStationId(rs.getInt(1));
                conf.setParameterId(rs.getInt(2));
                conf.setPeriodName(rs.getString(3));
                conf.setEnabled(rs.getInt(4));
                conf.setStationName(rs.getString(5));
                conf.setPeriodId(rs.getInt(6));
                configs.add(conf);
            }

        }catch (SQLException e)
        {
            e.printStackTrace();
        }

        return configs;
    }

    public List<SmhiPeriods> getSmhiPeriods() throws SQLException {
        List<SmhiPeriods> periodsList = new ArrayList<>();
        String sql ="Select PeriodId, PeriodName From Periods order by PeriodId";
        Connection thisConnection = useSQLite ? ConnectionManager.getSqliteConnected() : ConnectionManager.getConnected();

        ResultSet rs = thisConnection.createStatement().executeQuery(sql);

        while (rs.next()){
            SmhiPeriods periods = new SmhiPeriods();
            periods.setPeriodId(rs.getInt(1));
            periods.setPeriodName(rs.getString(2));

            periodsList.add(periods);
        }
        return periodsList;
    }
    public List<Stations> getStatonList() throws SQLException {
        List<Stations> stationsList = new ArrayList<>();

        String sql ="SELECT StationId, StationName, Latitud, Longitud, Height, FromDateTime, ToDateTime, Active FROM Stations";

        Connection thisConnection = useSQLite ? ConnectionManager.getSqliteConnected() : ConnectionManager.getConnected();

        ResultSet rs= thisConnection.createStatement().executeQuery(sql);

        while (rs.next()){
            Stations station = new Stations();
            station.setStationId(rs.getInt(1));
            station.setStationName(rs.getString(2));
            station.setLatitud(rs.getDouble(3));
            station.setLongitud(rs.getDouble(4));
            station.setHeight(rs.getDouble(5));
            station.setFromDateTime(rs.getLong(6));
            station.setToDateTime(rs.getLong(7));
            station.setActive(rs.getInt(8));
            stationsList.add(station);
        }


        return stationsList;

    }

    public void FetchAll(String argumentPeriod) throws SQLException, InterruptedException {
        Connection thisConnection = useSQLite ? ConnectionManager.getSqliteConnected() : ConnectionManager.getConnected();

        List<Stations> stationsList = new ArrayList<>();

        String sql ="select stationId, StationName from stations s  where s.StationId  not in (select StationId from data )";
        ResultSet rs= thisConnection.createStatement().executeQuery(sql);

        Inserter inserter = new Inserter();

        System.out.println("Requesting parameters...");
        JSONParse smhiApi = new JSONParse();

        String periodName = "";
        int periodId = 0;

        switch (argumentPeriod){
            case "-LM" :
                periodName = "latest-months";
                periodId=3;
            break;
            case "-LD" :
                periodName = "latest-day";
                periodId=2;
            break;
            case "-LH" :
                periodName = "latest-hour";
                periodId=1;
            break;
            case "-C" :
                periodId=4;
                periodName = "corrected-archive";
            break;
        }

        while (rs.next()){

            System.out.println("Hämtar " + rs.getString(2) + " ==>" + periodName);
            String data = smhiApi.getData("1", rs.getString(1), periodName);

            if (data == null) {
                System.out.println("No Data. Continues.");
                continue;
            }

            CsvScanner csvScanner = new CsvScanner(data);
            csvScanner.ScanCsv();


            inserter.save(csvScanner.getmTemperaturModel(), rs.getInt(1), 1, periodId);

            System.out.println("...Väntar 7 sek...");


            Thread.sleep(7000);

        }
        /*
        for (RunConfiguration conf : configurationList) {

                    //stationsList.stream().filter(h->h.getStationId() == conf.getStationId());
                    //Stations station = stationsList.stream().filter(j->j.getStationId() == conf.getStationId());
                    if (conf.getEnabled() == 1){

                        System.out.println("Data for station => ".concat( conf.getStationName()).concat(" period => ".concat(conf.getPeriodName() )));

                        String data = smhiApi.getData(conf.getParameterId().toString(), conf.getStationId().toString(), conf.getPeriodName());

                        if (data == null) {
                            System.out.println("No Data. Continues.");
                            continue;
                        }

                        CsvScanner csvScanner = new CsvScanner(data);
                        csvScanner.ScanCsv();

                        inserter.save(csvScanner.getmTemperaturModel(), conf.getStationId(), conf.getParameterId(), conf.getPeriodId());

                        System.out.println("...vantar...");

                        Thread.sleep(5000);

                    }



                }
         */

    }
}
