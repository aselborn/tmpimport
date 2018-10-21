package helper;


import java.time.LocalDateTime;

public abstract class MetadataInfo {

    private String stationsNamn;
    private String klimatNummer;
    private LocalDateTime locationStart;
    private LocalDateTime locationStop;
    private double latitude;
    private double longitude;

    public LocalDateTime getLocationStart() {
        return locationStart;
    }

    public void setLocationStart(LocalDateTime locationStart) {
        this.locationStart = locationStart;
    }

    public LocalDateTime getLocationStop() {
        return locationStop;
    }

    public void setLocationStop(LocalDateTime locationStop) {
        this.locationStop = locationStop;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private double height;

    public String getStationsNamn() {
        return stationsNamn;
    }

    public void setStationsNamn(String stationsNamn) {
        this.stationsNamn = stationsNamn;
    }

    public String getKlimatNummer() {
        return klimatNummer;
    }

    public void setKlimatNummer(String klimatNummer) {
        this.klimatNummer = klimatNummer;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
