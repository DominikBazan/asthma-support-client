package agh.asthmasupport.global;

public class GlobalStorage {

    public static final String baseUrl = "https://asthma-support-web-service.herokuapp.com/";
//    public static final String baseUrl = "https://app-name-cezar1.herokuapp.com/";
//    public static final String baseUrl = "http://localhost:5000/";

    public static String email;
    public static String password;

    public static void log(String value) {
        System.out.println(value);
    }

}
