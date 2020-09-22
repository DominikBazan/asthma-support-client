package agh.asthmasupport.global;

public class Encryption {

    private static int key = 98;

    private static String encryptChar(char ch) {
//        int ich = (int) ch;
//        int resInt = ich;
//        for (int i = 1; i <= key; i++) {
//            resInt += 1;
//            if (resInt == 127) {
//                resInt = 32;
//            }
//        }
//        return Character.toString((char) resInt);

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
//        int intCode = (int) sIntCode;
//        int resInt = intCode;
//        for (int i = 1; i <= key; i++) {
//            resInt -= 1;
//            if (resInt == 31) {
//                resInt = 126;
//            }
//        }
//        return Character.toString((char) resInt);

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
