import com.opencsv.CSVParser;
import dao.Fetcher;
import dao.Inserter;
import dao.SmhiParameters;
import dao.Stations;
import helper.ReadCSV;
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

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class ProgramTempimport {

    private static final Logger log=LoggerFactory.getLogger(ProgramTempimport.class);
    private static String inputDirectory = null;
    private static Fetcher mFetcher = new Fetcher();
    private static List<Stations> stationsList;
    public static void main(String[] args) throws IOException, SQLException {



        if (args.length==0){

            setupLogfile();
            setupLog();

            inputDirectory = Util.readConfiguration("inputdirectory");

            log.info("SMHI import, ver 0.1, starting " + DateTime.now());
            log.info("Watching directory => " + inputDirectory);

            new DownloadWatcher(inputDirectory, false).processEvents();

        } else {

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



                //String StationNames = smhiApi.getStationNames("27");
                //smhiApi.getPeriodNames("1", "71420");

                //String actualData = smhiApi.getData("1", "71420", "latest-months"); //Ger 4 senaste månaderna.
                //String actualData = smhiApi.getData("1", "71420", "corrected-archive"); //Ger 4 senaste månaderna.
                //https://opendata-download-metobs.smhi.se/api/version/latest/parameter/1/station/71420

                for (RunConfiguration conf : configurationList) {

                    //stationsList.stream().filter(h->h.getStationId() == conf.getStationId());
                    //Stations station = stationsList.stream().filter(j->j.getStationId() == conf.getStationId());

                    String data = smhiApi.getData(conf.getParameterId().toString(), conf.getStationId().toString(), conf.getName());
                    //String actualData = smhiApi.getData("1", "71420", "latest-months"); //Ger 4 senaste månaderna.
                    System.out.println(data);
                }


            }

            if (args[0].compareTo("--f") == 0){

                /*
                System.out.println("Input file: ");
                Console cn = System.console();
                String userFile =cn.readLine();
                System.out.println("Reading file " + userFile );
                */

                String input = "c:/temp/uddevalla.csv";
                File f = new File(input);
                f.exists();
                System.out.println(f.getAbsolutePath());

            }


        }





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
