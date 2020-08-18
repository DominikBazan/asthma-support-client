package agh.asthmasupport.communication;

import java.util.List;

import agh.asthmasupport.communication.objects.Message;
import agh.asthmasupport.communication.objects.TestResult;
import agh.asthmasupport.communication.objects.UserCredentials;
import agh.asthmasupport.communication.objects.UserData;
import retrofit2.Call;
import retrofit2.http.Body;
//import retrofit2.http.GET;
import retrofit2.http.POST;

public interface JsonPlaceHolderApi {

//    @GET("testingContents")
//    Call<List<ConnectionTest1>> getTestingContents();

    @POST("loginRequest")
    Call<List<Message>> loginUser(@Body UserCredentials user);

    @POST("registerRequest")
    Call<List<Message>> registerUser(@Body UserCredentials user);

    @POST("passwordHash")
    Call<List<Message>> getPasswordHash(@Body Message email);

    @POST("getUserData")
    Call<List<Message>> getUserData(@Body Message email);

    @POST("updateUserData")
    Call<List<Message>> updateUserData(@Body UserData email);

    @POST("changePassword")
    Call<List<Message>> changePassword(@Body UserCredentials user);

    @POST("addTestResult")
    Call<List<Message>> addTestResult(@Body TestResult testResult);

}