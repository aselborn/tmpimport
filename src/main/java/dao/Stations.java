package dao;

public class Stations {


    private int stationId;
    private String stationName;
    private double latitud;
    private double longitud;
    private Double height;
    private long fromDateTime;
    private long toDateTime;
    private int active;

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public long getFromDateTime() {
        return fromDateTime;
    }

    public void setFromDateTime(long fromDateTime) {
        this.fromDateTime = fromDateTime;
    }

    public long getToDateTime() {
        return toDateTime;
    }

    public void setToDateTime(long toDateTime) {
        this.toDateTime = toDateTime;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }
}
