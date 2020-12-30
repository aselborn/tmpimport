package model;

public class TemperaturData {
    private String datum;
    private String klockslag;
    private double temperatur;

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getKlockslag() {
        return klockslag;
    }

    public void setKlockslag(String klockslag) {
        this.klockslag = klockslag;
    }

    public double getTemperatur() {
        return temperatur;
    }

    public void setTemperatur(double temperatur) {
        this.temperatur = temperatur;
    }
}
