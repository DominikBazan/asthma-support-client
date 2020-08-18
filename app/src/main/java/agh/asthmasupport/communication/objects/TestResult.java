package agh.asthmasupport.communication.objects;

public class TestResult {

    private String email, points, date;

    public TestResult(String email, String points, String date) {
        this.email = email;
        this.points = points;
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public String getDate() {
        return date;
    }

    public String getPoints() {
        return points;
    }

}
