package helper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class Util {

    public static LocalDateTime getDateTime(String timevalue){

        DateTimeFormatter dtFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime myDateTime = LocalDateTime.parse(timevalue, dtFmt);

        return myDateTime;

    }

    public static String readConfiguration(String property) {
        Properties prop = new Properties();
        InputStream inps = null;
        try{

            inps = new FileInputStream("config.properties");
            prop.load(inps);

            return prop.getProperty(property);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            return null;

        }
    }
}
