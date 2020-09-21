package agh.asthmasupport.communication.objects;

public class NewAllergy {

    private String allergyName, email;

    public NewAllergy(String allergyName, String email) {
        this.allergyName = allergyName;
        this.email = email;
    }

    public String getAllergyName() {
        return allergyName;
    }

    public void setAllergyName(String allergyName) {
        this.allergyName = allergyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
