package agh.asthmasupport.communication.objects;

import java.util.ArrayList;

public class MedicinesNamesToDelete {

    private String email;
    private ArrayList<String> medicinesToDelete;

    public MedicinesNamesToDelete(String email, ArrayList<String> medicinesToDelete) {
        this.email = email;
        this.medicinesToDelete = medicinesToDelete;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getMedicinesToDelete() {
        return medicinesToDelete;
    }

    public void setMedicinesToDelete(ArrayList<String> medicinesToDelete) {
        this.medicinesToDelete = medicinesToDelete;
    }

}
