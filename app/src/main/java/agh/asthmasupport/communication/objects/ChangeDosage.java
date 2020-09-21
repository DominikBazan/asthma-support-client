package agh.asthmasupport.communication.objects;

public class ChangeDosage {

    private String email, mode;

    public ChangeDosage(String email, String mode) {
        this.email = email;
        this.mode = mode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

}
