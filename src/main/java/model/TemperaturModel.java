package model;

import java.util.ArrayList;
import java.util.List;

public class TemperaturModel extends BaseModel {

    private List<TemperaturData> mData = new ArrayList<>();

    public TemperaturModel(String prmNamn, String beskr, String menhet){
        parameterNamn=prmNamn;
        beskrivning=beskr;
        enhet=menhet;
    }

    public void setTemperatur(TemperaturData data) {
        mData.add(data);
    }

    public List<TemperaturData> getmData() {
        return mData;
    }
}
