package agh.asthmasupport.communication.objects;

public class DailyStatistics {

    private String date, value, implemented, rain, wind, temperature, dusting;

    public DailyStatistics(
            String date,
            String value,
            String implemented,
            String rain,
            String wind,
            String temperature,
            String dusting
    ) {
        this.date = date;
        this.value = value;
        this.implemented = implemented;
        this.rain = rain;
        this.wind = wind;
        this.temperature = temperature;
        this.dusting = dusting;
    }

    public String getDate() {
        return date;
    }

    public String getValue() {
        return value;
    }

    public String getImplemented() {
        return implemented;
    }

    public String getRain() {
        return rain;
    }

    public String getWind() { return wind; }

    public String getTemperature() { return temperature; }

    public String getDusting() { return dusting; }

    public void setDate(String date) {
        this.date = date;
    }

    public void setValue(String value) { this.value = value; }

    public void setImplemented(String implemented) {
        this.implemented = implemented;
    }

    public void setRain(String rain) {
        this.rain = rain;
    }

    public void setWind(String wind) { this.wind = wind; }

    public void setTemperature(String temperature) { this.temperature = temperature; }

    public void setDusting(String dusting) { this.dusting = dusting; }

}
