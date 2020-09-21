package agh.asthmasupport.communication.objects;

public class NewMedicine {

    private String medicineName, email;

    public NewMedicine(String medicineName, String email) {
        this.medicineName = medicineName;
        this.email = email;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
