import dao.Fetcher;
import dao.Inserter;
import dao.SmhiParameters;
import dao.Stations;
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

public class ProgramTempimport {

    private static final Logger log=LoggerFactory.getLogger(ProgramTempimport.class);
    private static String inputDirectory = null;
    private static Fetcher mFetcher = new Fetcher();
    private static List<Stations> stationsList;


    public static void main(String[] args) throws IOException, SQLException, InterruptedException {


        /*
            202102 endast växlar
         */

        List<String> argumentList = new ArrayList<>();
        argumentList.add("--w");
        argumentList.add("--s");
        argumentList.add("--smhi");
        argumentList.add("--d");

        if (args.length==0) {

            programInfo();

 /*           setupLogfile();
            setupLog();

            inputDirectory = Util.readConfiguration("inputdirectory");

            log.info("SMHI import, ver 0.1, starting " + DateTime.now());
            log.info("Watching directory => " + inputDirectory);

            new DownloadWatcher(inputDirectory, false).processEvents();
*/
        } else {

            String argument = args[0];

            if (!argumentList.contains(argument))
            {
                programInfo();
                return;
            }

            if (args[0].compareTo("--smhi") == 0){

                List<RunConfiguration> configurationList = mFetcher.getRunconfigList();
                stationsList = mFetcher.getStatonList();

                Inserter inserter = new Inserter();

                System.out.println("Requesting parameters...");
                JSONParse smhiApi = new JSONParse();

                if (mFetcher.TableCount("SmhiParameters") == 0){

                    List<SmhiParameters> smhiParameters = smhiApi.getConfiguredParameters();

                    inserter.setSmhiParameters(smhiParameters);
                    inserter.insertSmhiParameters();

                }

                if (stationsList.size() == 0){

                    stationsList = smhiApi.getStations("1");
                    inserter.setStationList(stationsList);
                    int rows = inserter.insertStations();

                }



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

                System.out.println("Resultat = " + result);


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
        System.out.println("\tHämta data: --smhi");
        System.out.println("\tLägg till station: --s");
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
