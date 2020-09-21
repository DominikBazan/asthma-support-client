package agh.asthmasupport.communication.objects;

public class Medicine {

    private String medicineName;

    public Medicine(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }
}
