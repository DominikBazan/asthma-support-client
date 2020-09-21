package agh.asthmasupport.communication;

import java.util.ArrayList;
import java.util.List;

import agh.asthmasupport.communication.objects.AllergiesNamesToDelete;
import agh.asthmasupport.communication.objects.Allergy;
import agh.asthmasupport.communication.objects.ChangeDosage;
import agh.asthmasupport.communication.objects.DailyStatistics;
import agh.asthmasupport.communication.objects.Medicine;
import agh.asthmasupport.communication.objects.MedicinesNamesToDelete;
import agh.asthmasupport.communication.objects.Message;
import agh.asthmasupport.communication.objects.NewAllergy;
import agh.asthmasupport.communication.objects.NewMedicine;
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

    @POST("getStatistics")
    Call<ArrayList<DailyStatistics>> getStatistics(@Body Message email);

    @POST("changeTodaysMedicineTakenState")
    Call<Message> changeTodaysMedicineTakenState(@Body Message email);

    @POST("getPrediction")
    Call<Message> getPrediction(@Body Message email);

    @POST("getColorInfo")
    Call<Message> getColorMedicinesCard(@Body Message email);

    @POST("addFirstMedicineEventAndTest")
    Call<Message> addFirstMedicineEventAndTest(@Body Message email);

    @POST("addMissingMedicineEventsAndTests")
    Call<Message> addMissingMedicineEventsAndTests(@Body Message email);

    @POST("getMedicines")
    Call<ArrayList<Medicine>> getMedicines(@Body Message email);

    @POST("addNewMedicine")
    Call<Message> getMedicines(@Body NewMedicine newMed);

    @POST("deleteMedicinesUsed")
    Call<Message> deleteMedicinesUsed(@Body MedicinesNamesToDelete medicinesToDelete);

    @POST("changeDosage")
    Call<Message> changeDosage(@Body ChangeDosage changeDosage);

    @POST("getUsersAllergies")
    Call<ArrayList<Message>> getUsersAllergies(@Body Message email);

    @POST("addNewAllergy")
    Call<Message> addNewAllergy(@Body NewAllergy newAllergy);

    @POST("deleteAllergiesUsed")
    Call<Message> deleteAllergiesUsed(@Body AllergiesNamesToDelete allergiesToDelete);

    @POST("getAllAsthmaFactors")
    Call<ArrayList<Message>> getAllAsthmaFactors(@Body Message email);



}