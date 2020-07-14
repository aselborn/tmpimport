import dao.Inserter;
import helper.ReadCSV;
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
import java.io.IOException;

public class ProgramTempimport {

    private static final Logger log=LoggerFactory.getLogger(ProgramTempimport.class);
    private static String inputDirectory = null;

    public static void main(String[] args) throws IOException {


        setupLogfile();
        setupLog();

        inputDirectory = Util.readConfiguration("inputdirectory");

        if (args.length==0){
            log.info("SMHI import, ver 0.1, starting " + DateTime.now());
            log.info("Watching directory => " + inputDirectory);

            new DownloadWatcher(inputDirectory, false).processEvents();

        } else {

            //Command given.

            if (args[0].compareTo("--smhi") == 0){

                log.info("Contacting SMHI.");
                JSONParse smhiApi = new JSONParse();
                String params = smhiApi.getParameters();

                System.out.println(params);

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
