package agh.asthmasupport.global;

import java.util.ArrayList;
import java.util.List;

import agh.asthmasupport.communication.objects.Allergy;
import agh.asthmasupport.communication.objects.DailyStatistics;
import agh.asthmasupport.communication.objects.Medicine;
import agh.asthmasupport.communication.objects.Message;

public class Encryption {

    private static int key = 98;

    private static String encryptChar(char ch) {
        int ich = (int) ch;
        return Character.toString((char) (ich + key));
    }

    public static String encrypt(String value) {
        StringBuilder encrypted = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            encrypted.append(encryptChar(ch));
        }
        return encrypted.toString();
    }

    public static String decryptChar(char sIntCode) {
        int intCode = (int) sIntCode;
        return Character.toString((char) (intCode - key));
    }

    public static String decrypt(String encrypted) {
        StringBuilder decrypted = new StringBuilder();
        for (int i = 0; i < encrypted.length(); i++) {
            char ch = encrypted.charAt(i);
            decrypted.append(decryptChar(ch));
        }
        return decrypted.toString();
    }

    public static void decryptArrayListOfDailyStatistics(ArrayList<DailyStatistics> dailyStatistics) {
        for (DailyStatistics m : dailyStatistics) {
            m.setDate(decrypt(m.getDate()));
            m.setValue(decrypt(m.getValue()));
            m.setImplemented(decrypt(m.getImplemented()));
            m.setRain(decrypt(m.getRain()));
            m.setWind(decrypt(m.getWind()));
            m.setTemperature(decrypt(m.getTemperature()));
            m.setDusting(decrypt(m.getDusting()));
        }
    }

    public static void decryptArrayListOfMedicines(ArrayList<Medicine> medicines) {
        for (Medicine m : medicines) {
            m.setMedicineName(decrypt(m.getMedicineName()));
        }
    }

    public static void decryptArrayListOfMessages(ArrayList<Message> messages) {
        for (Message m : messages) {
            m.setText(decrypt(m.getText()));
        }
    }

    public static void main(String[] args) {
        String start = "Sekretny komunikat: !\"#$%&'()*+??-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~ ąęćśżźóĄĘĆŻŹŚÓ";

        System.out.println("Start: \t\t" + start);

        String encrypted = Encryption.encrypt(start);

        System.out.println("Encrypted:\t" + encrypted);

        String decrypted = Encryption.decrypt(encrypted);

        System.out.println("Decrypted:\t" + decrypted);

        System.out.println("TEST:\t\t" + decrypted.equals(start));
    }

}
