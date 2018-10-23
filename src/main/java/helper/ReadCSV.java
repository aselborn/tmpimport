package helper;

import com.opencsv.CSVReader;



import java.io.FileReader;
import java.io.IOException;
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

    private static int YEAR_MONTH_TEXT =2;
    private static int MONTH_TEMP=3;


    private static int OTHER=3;

    private static String PARAMETERNAMN = "Parameternamn";
    private static String START = "Datum";
    private static String START2 = "Från Datum Tid (UTC)";

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
        boolean parseParameters=false;

        int stepCount = 0;

        for (String[] data : listData){

            if (parseTimePeriod){

                String[] rows = data[0].replace(",", " ").split(";");
                if (!rows[0].isEmpty()){

                    if (stepCount==0) { //startdat
                        temperatureObject.setLocationStart(Util.getDateTime(rows[DATE_STRING]));
                        temperatureObject.setLocationStop(Util.getDateTime(rows[TIME_STRING]));
                        temperatureObject.setLatitude(Double.parseDouble(rows[LATITUDE]));
                        temperatureObject.setLongitude(Double.parseDouble(rows[LONGITUDE]));

                    }

                    temperatureObject.setLocationStop(Util.getDateTime(rows[TIME_STRING]));


                } else{
                    parseTimePeriod = false;
                }
                stepCount++;
                continue;
            }

            if (parseParameters){
                String[] rows = data[0].split(";");

                //List<String> params = Arrays.asList(data[0].split("\\s*,\\s*"));

                if (!rows[0].isEmpty()){
                    temperatureObject.setParameterName(rows[0]);

                    if (data.length==2){
                        String info = data[1].split(";")[0];
                        temperatureObject.setBeskrivning(info);

                        if (info.contains("månad"))
                            temperatureObject.setIsMonthAvarage(true);
                    }


                } else {
                    parseParameters=false;
                }
                continue;
            }

            if (parseData) {
                //Reading actually data...
                String[] rows = data[0].split(";");

                TemperatureCSV temperatureCSV = new TemperatureCSV();

                //Month or momentan?
                if (temperatureObject.isMonthAvarage()){

                    String[] yearMonth = rows[YEAR_MONTH_TEXT].split("-");
                    int y = Integer.parseInt(yearMonth[0]);
                    int m = Integer.parseInt(yearMonth[1]);

                    temperatureCSV.setDatString(rows[YEAR_MONTH_TEXT]);
                    temperatureCSV.setTempString(rows[MONTH_TEMP]);
                    temperatureCSV.setRepYear(y);;
                    temperatureCSV.setRepMonth(m);

                }else {
                    temperatureCSV.setDatString(rows[DATE_STRING]);
                    temperatureCSV.setTimeString(rows[TIME_STRING]);
                    temperatureCSV.setTempString(rows[TEMP_STRING]);
                    temperatureCSV.setOther(rows[OTHER]);

                }

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

                //STart of actual data
                if ( (rows[0].compareTo(START) == 0) || rows[0].compareTo(START2) == 0){
                    parseData=true;
                }

                if (rows[0].compareTo("Tidsperiod (fr.o.m)") == 0){
                    parseTimePeriod=true;
                }

                if (rows[0].compareTo(PARAMETERNAMN) == 0){
                    parseParameters=true;
                }


                rowCount++;

            }

        }

    }


    public TemperatureObject getTemperatureObject() {
        return temperatureObject;
    }
}
