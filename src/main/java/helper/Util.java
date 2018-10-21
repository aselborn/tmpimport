package helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Util {

    public static LocalDateTime getDateTime(String timevalue){

        DateTimeFormatter dtFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime myDateTime = LocalDateTime.parse(timevalue, dtFmt);

        return myDateTime;

    }


}
