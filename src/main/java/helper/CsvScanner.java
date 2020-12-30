package helper;

import model.TemperaturData;
import model.TemperaturModel;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class CsvScanner {

    private final String mData;
    private final String prmNamn = "Parameternamn";
    private final String prmBeskrivning = "Beskrivning";
    private final String prmEnhet = "Enhet";

    public CsvScanner(String data){
        mData = data;
    }
    private TemperaturModel mTemperaturModel ;

    public TemperaturModel getmTemperaturModel() {
        return mTemperaturModel;
    }

    public void ScanCsv(){

        int row = 0;
        boolean isPeriod = false;
        boolean isData = false;


        String adjusted = mData.replaceAll("(?m)^[ \t]*\r?\n", "");
        String[] st = adjusted.split("\n");

        List<String> datas = Arrays.asList(st);

        for (String s: datas) {
            row++;
            String r[] = s.split(";");

            if (isData){

                String rowData[] = s.split(";");

                TemperaturData data = new TemperaturData();

                if (rowData[0].length()==0 || rowData[1].length() == 0 || rowData[2].length() == 0)
                    break;

                data.setDatum(rowData[0]);
                data.setKlockslag(rowData[1]);
                data.setTemperatur(Double.parseDouble(rowData[2]));
                mTemperaturModel.setTemperatur(data);

            }

            if (isPeriod){
                mTemperaturModel = new TemperaturModel(r[0], r[1], r[2]);
                isPeriod=false;
            }

            if (r[0].toLowerCase(Locale.ROOT).equals(prmNamn.toLowerCase(Locale.ROOT))){
                isPeriod=true;
            }

            if  (r[0].toLowerCase(Locale.ROOT).contains("datum")){
                isData=true;
            }

        }


    }

}
