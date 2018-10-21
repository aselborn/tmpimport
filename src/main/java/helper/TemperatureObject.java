package helper;

import java.util.ArrayList;
import java.util.List;

public class TemperatureObject extends MetadataInfo {

    private ArrayList<TemperatureCSV> temperatureCSVList= new ArrayList<>();

    public ArrayList<TemperatureCSV> getTemperatureCSVList() {
        return temperatureCSVList;
    }

    public void setTemperatureCSV(TemperatureCSV csv){
        temperatureCSVList.add(csv);
    }
}
