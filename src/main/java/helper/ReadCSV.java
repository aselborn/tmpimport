package helper;

import com.opencsv.CSVReader;



import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class ReadCSV {

    private String m_fileToRead = null;

    private static int STATION = 0;
    private static int KLIMATNUMMER=1;
    private static int MASL = 2;
    private static int LATITUDE=3;
    private static int LONGITUDE=4;


    private static int DATE_STRING=0;
    private static int TIME_STRING=1;
    private static int TEMP_STRING=2;
    private static int OTHER=3;

    private TemperatureObject temperatureObject;



    public ReadCSV(String fileToRead){
        m_fileToRead=fileToRead;
        temperatureObject = new TemperatureObject();
    }

    public void Read() throws IOException {

        CSVReader csvReader = new CSVReader(new FileReader(m_fileToRead));

        List<String[]> listData = csvReader.readAll();

        int rowCount =0;
        boolean parseData=false;
        boolean parseTimePeriod=false;

        int stepCount = 0;

        for (String[] data : listData){

            if (parseTimePeriod){

                String[] rows = data[0].split(";");
                if (!rows[0].isEmpty()){

                    if (stepCount==0) { //startdat
                        temperatureObject.setLocationStart(Util.getDateTime(rows[DATE_STRING]));
                        temperatureObject.setLocationStop(Util.getDateTime(rows[TIME_STRING]));
                        temperatureObject.setLatitude(Double.parseDouble(rows[LATITUDE]));
                        temperatureObject.setLongitude(Double.parseDouble(rows[LONGITUDE]));

                    }

                    temperatureObject.setLocationStart(Util.getDateTime(rows[DATE_STRING]));


                } else{
                    parseTimePeriod = false;
                }
                stepCount++;
                continue;
            }

            if (parseData) {
                //Reading actually data...
                String[] rows = data[0].split(";");

                TemperatureCSV temperatureCSV = new TemperatureCSV();

                temperatureCSV.setDatString(rows[DATE_STRING]);
                temperatureCSV.setTimeString(rows[TIME_STRING]);
                temperatureCSV.setTempString(rows[TEMP_STRING]);
                temperatureCSV.setOther(rows[OTHER]);

                temperatureObject.setTemperatureCSV(temperatureCSV);
                rowCount++;

                continue;
            }

            if (data.length == 0){
                break;
            }

            String[] rows = data[0].split(";");

            if (rows[0].length()>0){

                if (rowCount == 1){
                    //First.
                    temperatureObject.setStationsNamn(rows[STATION]);
                    temperatureObject.setKlimatNummer(rows[KLIMATNUMMER]);
                    temperatureObject.setHeight(Double.parseDouble(rows[MASL]));

                }

                if (rows[0].compareTo("Tidsperiod (fr.o.m)") == 0){
                    parseTimePeriod=true;
                }

                if (rows[0].compareTo("Datum") == 0){
                    parseData=true;
                }


                rowCount++;

            }

        }

    }


    public TemperatureObject getTemperatureObject() {
        return temperatureObject;
    }
}
