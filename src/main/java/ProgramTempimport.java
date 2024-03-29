import dao.Fetcher;
import dao.Inserter;
import dao.SmhiParameters;
import dao.Updater;
import helper.CreateSQLiteDB;
import model.SmhiPeriods;
import model.Stations;
import helper.CsvScanner;
import helper.RunConfiguration;
import helper.Util;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.xml.DOMConfigurator;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smhi.JSONParse;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class ProgramTempimport {

    private static final Logger log=LoggerFactory.getLogger(ProgramTempimport.class);
    private static String inputDirectory = null;
    private static Fetcher mFetcher = new Fetcher();
    private static List<Stations> stationsList;

    private static JSONParse smhiApi = new JSONParse();
    private static Inserter inserter = new Inserter();
    private int rowCount = 0;
    private static boolean useSQLite = Boolean.parseBoolean(Util.readConfiguration("usesqlite"));

    public static void main(String[] args) throws IOException, SQLException, InterruptedException {
        /*
            202102 endast växlar
         */

        List<String> argumentList = new ArrayList<>();
        argumentList.add("--all");
        argumentList.add("--s"); //Ny station
        argumentList.add("--smhi"); //Hämta data via SMHI
        argumentList.add("--d"); //Ta bort station
        argumentList.add("--v"); //Lista stationer
        argumentList.add("--p"); //Lista vad perioder betyder
        argumentList.add("--x"); //Stäng av alla stationer som hämtas (runconfig 0)
        argumentList.add("--y"); //Hämta en viss station

        List<String> subArgumentList = new ArrayList<>();
        subArgumentList.add("-C");
        subArgumentList.add("-LM");
        subArgumentList.add("-LD");
        subArgumentList.add("-LH");


        setupDataBase();
        checkStationList();


        if (args.length==0) {
            programInfo();
        } else {

            String argument = args[0];

            if (!argumentList.contains(argument))
            {
                programInfo();
                return;
            }

            if (args[0].compareTo("--all") == 0) {

                String select = "";

                if (args.length < 2){
                    System.out.println("ALla stationer för viss parameter hämtas. Växel måste anges");
                    System.out.println("-C (Corrected Archive)");
                    System.out.println("-LM (Latest Months)");
                    System.out.println("-LD (Latest Days)");
                    System.out.println("-LH (Latest Hours)");
                    return;
                } else {

                    select = args[1].toString();
                    if (!subArgumentList.contains(select)){
                        System.out.println("Ej giltligt tilläggsargument.");
                        return;
                    }
                }

                new Fetcher().FetchAll(select);
            }

            if (args[0].compareTo("--smhi") == 0){

                List<RunConfiguration> configurationList = mFetcher.getRunconfigList();
                stationsList = mFetcher.getStatonList();
                System.out.println("Requesting parameters...");


                if (mFetcher.TableCount("SmhiParameters") == 0){

                    List<SmhiParameters> smhiParameters = smhiApi.getConfiguredParameters();

                    inserter.setSmhiParameters(smhiParameters);
                    inserter.insertSmhiParameters();

                }

                checkStationList();
                //String actualData = smhiApi.getData("1", "71420", "latest-months"); //Ger 4 senaste månaderna.
                //String actualData = smhiApi.getData("1", "71420", "corrected-archive"); //Ger 4 senaste månaderna.
                //https://opendata-download-metobs.smhi.se/api/version/latest/parameter/1/station/71420

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


            }
            //WATCH
            if (args[0].compareTo("--w") == 0){

                setupLogfile();
                setupLog();

                inputDirectory = Util.readConfiguration("inputdirectory");

                log.info("SMHI import, ver 0.1, starting " + DateTime.now());
                log.info("Watching directory => " + inputDirectory);

                new DownloadWatcher(inputDirectory, false).processEvents();

            }

            //Ny station
            if (args[0].compareTo("--s") == 0){
               System.out.println("Lägga till ny station");

                Scanner sc = new Scanner(System.in);
                System.out.print("\tStationID : ");
                int stationId = Integer.valueOf(sc.nextLine());

                Inserter inserter = new Inserter();
                String result = inserter.insertStation(stationId);

            }
            //Delete station
            if (args[0].compareTo("--d") == 0){
                System.out.println("Ta bort en station");

                Scanner sc = new Scanner(System.in);
                System.out.print("\tStationID : ");
                int stationId = Integer.valueOf(sc.nextLine());

                Inserter inserter = new Inserter();
                String result = inserter.deleteStation(stationId);

                System.out.println("Resultat = " + result);

            }

            //Visa stationer som hämtas (-v)
            if(args[0].compareTo("--v") == 0 ){

                System.out.println("Visar alla stationer som skall hämtas.");

                Fetcher fetcher = new Fetcher();
                List<Stations> myStations = fetcher.getStatonList();

                List<RunConfiguration> config =  fetcher.getRunconfigList();

                for (RunConfiguration c: config) {
                    if (c.getEnabled() == 1)
                        System.out.println("Hämtar station ".concat(c.getStationName()).concat(" parameter ".concat(c.getPeriodName())).concat(" stationId > ".concat(c.getStationId().toString())));
                }

            }

            if (args[0].compareTo("--p") == 0){
                System.out.println("Perioder ");
                List<SmhiPeriods> periods = new Fetcher().getSmhiPeriods();
                for(SmhiPeriods per : periods){
                    System.out.println("Id: " + per.getPeriodId() + ", Period: = " + per.getPeriodName());
                }
            }

            if (args[0].compareTo("--x") == 0){
                System.out.println("Stänger alla hämtningar.");
               new Updater().CloseAllRuns();
            }

            if (args[0].compareTo("--y") == 0){

                Console cnsl = System.console();
                if (cnsl == null){
                    System.out.println("Ingen konsol!.");
                    return;
                }

                String str = cnsl.readLine("Vilken station ?: ");
                String period = cnsl.readLine("Vilken period 1, 2, 3, 4 eller alla (A) ? :");

                System.out.println("Du vill hämta för ".concat(str).concat(" och för perioden ").concat(period));

                if (period.equals("A")){
                    period = "5";
                }

                new Updater().UpdateRunconfig(Integer.parseInt(str), Integer.parseInt(period));

            }
        }
    }


    private static void setupDataBase() {
        if (!useSQLite) return;

        String sqliteDB = Util.readConfiguration("dbname");

        if (! new File(sqliteDB).exists()) {
            CreateSQLiteDB.createDB(sqliteDB);

            CreateSQLiteDB.createTableData(sqliteDB);
            CreateSQLiteDB.createTableRunConfig(sqliteDB);
            CreateSQLiteDB.createTablePeriods(sqliteDB);
            CreateSQLiteDB.createTableSmhiParameters(sqliteDB);
            CreateSQLiteDB.createTableStations(sqliteDB);

            System.out.println("Databas och tabeller skapade.");

            CreateSQLiteDB.insertPeriods(sqliteDB);

        }


    }

    private static void checkStationList() throws IOException, SQLException {
        if (mFetcher.getStatonList().size() == 0){

            stationsList = smhiApi.getStations("1");

            if (stationsList.size() > 0){
                System.out.println("Hämtar stationer!");
                inserter.setStationList(stationsList);
                int rows = inserter.insertStations();
                System.out.println("Hämtade och sparade " + rows + " stationer.");
            } else{

            }
        }

    }

    private static void programInfo() {

        System.out.println();
        System.out.println("Temperaturimport, ett litet program som importerar temperaturer från SMHI.");
        System.out.println("Vänligen ange korrekt växel.");
        System.out.println("Växel anges med två minustecken samt kommando exempelvis [--w]");
        System.out.println("Tillgängliga växlar:");
        System.out.println();
        System.out.println("Vänligen ange korrekt växel.");
        System.out.println("\tBevaka mapp: --w");
        System.out.println("\tHämta all för parameter data: --all");
        System.out.println("\tHämta data: --smhi");
        System.out.println("\tLägg till station: --s");
        System.out.println("\tVisa stationer som hämtas: --v");
        System.out.println("\tVisa perioder som hämtas: --p");
        System.out.println("\tStäng av alla hämtningar: --x");
        System.out.println("\tAnge station som skall hämtas: --y");
        System.out.println();

    }

    private static void setupLog(){

        org.apache.log4j.Logger rootLogger = org.apache.log4j.Logger.getRootLogger();
        rootLogger.addAppender(new ConsoleAppender(new PatternLayout(PatternLayout.DEFAULT_CONVERSION_PATTERN), ConsoleAppender.SYSTEM_ERR));
        LogManager.getRootLogger().setLevel(Level.INFO);

    }

    private static void setupLogfile(){
        DOMConfigurator.configure(Util.readConfiguration("log4jConf"));
    }
}
