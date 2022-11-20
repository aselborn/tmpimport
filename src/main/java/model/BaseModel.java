package model;

public abstract class BaseModel {

    public String parameterNamn;
    public String beskrivning;
    public String enhet ;

    public String getBeskrivning() {
        return beskrivning;
    }

    public void setBeskrivning(String beskrivning) {
        this.beskrivning = beskrivning;
    }

    public String getEnhet() {
        return enhet;
    }

    public void setEnhet(String enhet) {
        this.enhet = enhet;
    }

    public String getParameterNamn() {
        return parameterNamn;
    }

    public void setParameterNamn(String parameterNamn) {
        this.parameterNamn = parameterNamn;
    }
}
