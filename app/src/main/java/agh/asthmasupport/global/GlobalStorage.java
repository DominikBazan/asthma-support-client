package agh.asthmasupport.global;

public class GlobalStorage {

//    public static final String baseUrl = "192.168.43.136/";
//    public static final String baseUrl = "192.168.43.136:5000/";
//    public static final String baseUrl = "192.168.11.12:5000/";
    public static final String baseUrl = "https://asthma-support-web-service.herokuapp.com/";
//    public static final String baseUrl = "http://localhost:5000/";
//    public static final String baseUrl = "192.168.11.12/";
//    public static final String routeLogin = "login";

    public static String email;
    public static String password;

    public static void log(String value) {
        System.out.println(value);
    }

}
