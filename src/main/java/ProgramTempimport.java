import dao.Inserter;
import helper.ReadCSV;

import java.io.IOException;

public class ProgramTempimport {

    public static void main(String[] args) throws IOException {

        System.out.println("Hello World!");



        //ReadCSV readCSV = new ReadCSV("/home/anders/code/tmpimport/data/data.csv");
        ReadCSV readCSV = new ReadCSV("/home/anders/Downloads/smhi-opendata_1_180940_20181021_150055.csv");

        readCSV.Read();

        System.out.println("CSV file was read, rows = " + readCSV.getTemperatureObject().getTemperatureCSVList().size());
        System.out.println("Inserting rows for, " + readCSV.getTemperatureObject().getStationsNamn());

        //Save to database...
        Inserter inserter = new Inserter(readCSV.getTemperatureObject());
        inserter.insertData();

        System.out.println("File insterted.");

    }

}
