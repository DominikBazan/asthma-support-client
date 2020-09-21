package agh.asthmasupport.communication.objects;

import java.util.ArrayList;

public class AllergiesNamesToDelete {

    private String email;
    private ArrayList<String> allergiesToDelete;

    public AllergiesNamesToDelete(String email, ArrayList<String> allergiesToDelete) {
        this.email = email;
        this.allergiesToDelete = allergiesToDelete;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getAllergiesToDelete() {
        return allergiesToDelete;
    }

    public void setAllergiesToDelete(ArrayList<String> allergiesToDelete) {
        this.allergiesToDelete = allergiesToDelete;
    }

}
