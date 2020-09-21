package agh.asthmasupport.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import agh.asthmasupport.R;
import agh.asthmasupport.communication.JsonPlaceHolderApi;
import agh.asthmasupport.communication.objects.Message;
import agh.asthmasupport.communication.objects.UserCredentials;
import agh.asthmasupport.communication.objects.UserData;
import agh.asthmasupport.global.BCryptOperations;
import agh.asthmasupport.global.DatePickerFragment;
import agh.asthmasupport.global.GlobalStorage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainMenuActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private JsonPlaceHolderApi jsonPlaceHolderApi;

    private CardView cardViewPersonalData, cardViewAllergies, cardViewTest, cardViewStatistic;
    private CardView cardViewMedicinesTaken, cardViewMedicinesInfo;

    private TextView predictionTextView;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialogPersonalData, dialogChangePassword;

    private View personalDataPopupView;
    private EditText nameContent, surnameContent, heightContent, weightContent;
    private RadioGroup radioGroupSex;
    private RadioButton radioButton, radioNo, radioK, radioM;
    private Button okButton, cancelButton, changePasswordOptionButton;
    private TextView dateBirth, dateDiseaseStart;
    private String whatDateChanged = null;
    private Integer birthDay = null, birthMonth = null, birthYear = null;
    private Integer dStartDay = null, dStartMonth = null, dStartYear = null;

    private View changePasswordPopupView;
    private EditText newPasswordContent;
    private Button changePasswordButton, cancelChangePasswordButton;

    private Boolean medicinesTaken = false;
    private TextView medicinesMessageContent;


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main_menu);

        cardViewPersonalData = (CardView) findViewById(R.id.card_view_personal_data);
        cardViewPersonalData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPersonalDataDialog();
            }
        });

        cardViewAllergies = (CardView) findViewById(R.id.card_view_allergies);
        cardViewAllergies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, AllergiesActivity.class);
                startActivity(intent);
            }
        });

        cardViewTest = (CardView) findViewById(R.id.card_view_test);
        cardViewTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, TestActivity.class);
                startActivity(intent);
            }
        });

        cardViewStatistic = (CardView) findViewById(R.id.card_view_statistic);
        cardViewStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, StatisticActivity.class);
                startActivity(intent);
            }
        });

        cardViewMedicinesTaken = (CardView) findViewById(R.id.card_view_medicines_taken);
        cardViewMedicinesTaken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(GlobalStorage.baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

                Message medicineStateChange = new Message(GlobalStorage.email);

                Call<Message> call = jsonPlaceHolderApi.changeTodaysMedicineTakenState(medicineStateChange);
                call.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        if (!response.isSuccessful()) {
                            toastMessage("Error code: " + response.code());
                            return;
                        }
                        Message message = response.body();
                        String m = message.getText();
                        if (m.equals("YES")) {
                            medicinesTaken = true;
                            if (medicinesTaken) {
                                cardViewMedicinesTaken.setCardBackgroundColor(ContextCompat.getColor(MainMenuActivity.this, R.color.green));
                                medicinesMessageContent.setText("Leki :)");
                            } else if (!medicinesTaken) {
                                cardViewMedicinesTaken.setCardBackgroundColor(ContextCompat.getColor(MainMenuActivity.this, R.color.red));
                                medicinesMessageContent.setText("Leki !!!");
                            }
                        } else if (m.equals("NO")) {
                            medicinesTaken = false;
                            if (!medicinesTaken) {
                                cardViewMedicinesTaken.setCardBackgroundColor(ContextCompat.getColor(MainMenuActivity.this, R.color.red));
                                medicinesMessageContent.setText("Leki !!!");
                            } else if (medicinesTaken) {
                                cardViewMedicinesTaken.setCardBackgroundColor(ContextCompat.getColor(MainMenuActivity.this, R.color.green));
                                medicinesMessageContent.setText("Leki :)");
                            }
                        } else {
                            toastMessage("Wystąpił błąd.\nSpróbuj ponownie.");
                        }
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {
                        toastMessage("Error: " + t.getMessage());
                    }
                });
            }
        });

        cardViewMedicinesInfo = (CardView) findViewById(R.id.medicines_list_card);
        cardViewMedicinesInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, MedicinesActivity.class);
                startActivity(intent);
            }
        });

        medicinesMessageContent = (TextView) findViewById(R.id.medicinesMessageContent);
        medicinesMessageContent.setText("Leki");

        getInfoMedicinesTakenAndSetColor();

        predictionTextView = (TextView) findViewById(R.id.prediction_text_view);
        predictionTextView.setText("Na razie brak danych");

        getPrediction();

        setTitle(getTitle() + " - " + GlobalStorage.email);
    }

    //INFO: Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_option:
                // INFO: Logout action
                GlobalStorage.email = "";
                GlobalStorage.password = "";
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // INFO: Personal data
    public void createPersonalDataDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        personalDataPopupView = getLayoutInflater().inflate(R.layout.personal_data_popup, null);
        nameContent = (EditText) personalDataPopupView.findViewById(R.id.name_content);
        surnameContent = (EditText) personalDataPopupView.findViewById(R.id.surname_content);
        radioGroupSex = personalDataPopupView.findViewById(R.id.radio_group);
        radioNo = personalDataPopupView.findViewById(R.id.radio_no);
        radioK = personalDataPopupView.findViewById(R.id.radio_k);
        radioM = personalDataPopupView.findViewById(R.id.radio_m);
        dateBirth = (TextView) personalDataPopupView.findViewById(R.id.date_birth);
        dateBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whatDateChanged = "birth";
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
        heightContent = (EditText) personalDataPopupView.findViewById(R.id.height_content);
        weightContent = (EditText) personalDataPopupView.findViewById(R.id.weight_content);
        dateDiseaseStart = (TextView) personalDataPopupView.findViewById(R.id.date_disease_start);
        dateDiseaseStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whatDateChanged = "diseaseStart";
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalStorage.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Message emailMessage = new Message(GlobalStorage.email);

        Call<List<Message>> call = jsonPlaceHolderApi.getUserData(emailMessage);
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (!response.isSuccessful()) {
                    toastMessage("Error code: " + response.code());
                    return;
                }
                List<Message> messages = response.body();
                if (messages.get(0).getText() != null && messages.get(0).getText().equals("Error")) {
                    toastMessage("Wystąpił błąd.\nSpróbuj jeszcze raz.");
                    return;
                }
                nameContent.setText(messages.get(0).getText());
                surnameContent.setText(messages.get(1).getText());
                String sex = messages.get(2).getText();
                if (sex.equals("N")) radioNo.toggle();
                else if (sex.equals("K")) radioK.toggle();
                else if (sex.equals("M")) radioM.toggle();
                else toastMessage("Błąd związany z płcią.");
                String val = messages.get(3).getText();
                if (val != null) dateBirth.setText(val);
                heightContent.setText(messages.get(4).getText());
                weightContent.setText(messages.get(5).getText());
                val = messages.get(6).getText();
                if (val != null) dateDiseaseStart.setText(val);
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                toastMessage("Error: " + t.getMessage());
            }
        });

        dialogBuilder.setView(personalDataPopupView);
        dialogPersonalData = dialogBuilder.create();
        dialogPersonalData.show();

        okButton = (Button) personalDataPopupView.findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameContent.getText().toString();
                String surname = surnameContent.getText().toString();
                int radioId = radioGroupSex.getCheckedRadioButtonId();
                radioButton = personalDataPopupView.findViewById(radioId);
                String sex = (String) radioButton.getText();
                if (sex.equals("Nieokreślona")) sex = "N";
                else if (sex.equals("Kobieta")) sex = "K";
                else if (sex.equals("Mężczyzna")) sex = "M";
                else {
                    toastMessage("Błąd płci");
                    return;
                }
                String dateBirth;
                if (birthYear == null) dateBirth = "";
                else dateBirth = birthYear + "-" + birthMonth + "-" + birthDay;
                String height = heightContent.getText().toString();
                String weight = weightContent.getText().toString();
                String dStartDisease;
                if (dStartYear == null) dStartDisease = "";
                else dStartDisease = dStartYear + "-" + dStartMonth + "-" + dStartDay;

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(GlobalStorage.baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

                UserData userData = new UserData(name, surname, sex, GlobalStorage.email,
                        dateBirth, height, weight, dStartDisease);

                toastMessage("Zapisywanie . . .");
                Call<List<Message>> call = jsonPlaceHolderApi.updateUserData(userData);
                call.enqueue(new Callback<List<Message>>() {
                    @Override
                    public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                        if (!response.isSuccessful()) {
                            toastMessage("Error code: " + response.code());
                            return;
                        }
                        List<Message> messages = response.body();
                        String m = messages.get(0).getText();
                        toastMessage(m);
                        dialogPersonalData.dismiss();
                    }

                    @Override
                    public void onFailure(Call<List<Message>> call, Throwable t) {
                        toastMessage("Error: " + t.getMessage());
                    }
                });

            }
        });

        cancelButton = (Button) personalDataPopupView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPersonalData.dismiss();
            }
        });

        changePasswordOptionButton = (Button) personalDataPopupView.findViewById(R.id.change_password_option_button);
        changePasswordOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChangePasswordDialog();
                dialogPersonalData.dismiss();
            }
        });

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (whatDateChanged.equals("birth")) {
            birthDay = dayOfMonth;
            birthMonth = month;
            birthYear = year;
        } else if (whatDateChanged.equals("diseaseStart")) {
            dStartDay = dayOfMonth;
            dStartMonth = month;
            dStartYear = year;
        } else {
            // this should never execute
            toastMessage("Wystąpił błąd. Spróbuj ponownie.");
        }
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.DEFAULT).format(c.getTime());
        TextView dateBirth = (TextView) personalDataPopupView.findViewById(R.id.date_birth);
        if (whatDateChanged.equals("birth")) {
            dateBirth.setText(currentDateString);
        } else if (whatDateChanged.equals("diseaseStart")) {
            dateDiseaseStart.setText(currentDateString);
        } else {
            // this should never execute
            toastMessage("Wystąpił błąd. Spróbuj ponownie.");
        }
        whatDateChanged = null;
    }

    // INFO: Change password
    public void createChangePasswordDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        changePasswordPopupView = getLayoutInflater().inflate(R.layout.change_password_popup, null);
        newPasswordContent = (EditText) changePasswordPopupView.findViewById(R.id.new_password_content);
        changePasswordButton = (Button) changePasswordPopupView.findViewById(R.id.ok_change_password_button);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = newPasswordContent.getText().toString();
                if (newPassword.isEmpty()) {
                    toastMessage("Podaj hasło");
                    return;
                }
                if (newPassword.length() < 6) {
                    toastMessage("Hasło powinno zawierać przynamniej 6 znaków");
                    return;
                }
                String encryptedPassword = BCryptOperations.generateHashedPass(newPassword);
                UserCredentials user = new UserCredentials(GlobalStorage.email, encryptedPassword);
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(GlobalStorage.baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
                Call<List<Message>> call = jsonPlaceHolderApi.changePassword(user);
                call.enqueue(new Callback<List<Message>>() {
                    @Override
                    public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                        if (!response.isSuccessful()) {
                            toastMessage("Error code: " + response.code());
                            return;
                        }

                        List<Message> messages = response.body();
                        String m1 = messages.get(0).getText();

                        if (m1.equals("Zmieniono")) {
                            toastMessage(m1);
                            dialogChangePassword.dismiss();
                        } else if (m1.equals("Error")) {
                            toastMessage(m1);
                        } else {
                            toastMessage("Failure. Try again or contact administrator.");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Message>> call, Throwable t) {
                        toastMessage("Error: " + t.getMessage());
                    }
                });

            }
        });

        cancelChangePasswordButton = (Button) changePasswordPopupView.findViewById(R.id.cancel_change_button);
        cancelChangePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChangePassword.dismiss();
            }
        });

        dialogBuilder.setView(changePasswordPopupView);
        dialogChangePassword = dialogBuilder.create();
        dialogChangePassword.show();
    }

    private void getInfoMedicinesTakenAndSetColor() {
        Message emailMess = new Message(GlobalStorage.email);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalStorage.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<Message> call = jsonPlaceHolderApi.getColorMedicinesCard(emailMess);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (!response.isSuccessful()) {
                    toastMessage("Error code: " + response.code());
                    return;
                }
                Message message = response.body();
                String m = message.getText();
                if (m.equals("NO")) {
                    medicinesTaken = false;
                    cardViewMedicinesTaken.setCardBackgroundColor(ContextCompat.getColor(MainMenuActivity.this, R.color.red));
                    medicinesMessageContent.setText("Leki !!!");
                } else if (m.equals("YES")) {
                    medicinesTaken = true;
                    cardViewMedicinesTaken.setCardBackgroundColor(ContextCompat.getColor(MainMenuActivity.this, R.color.green));
                    medicinesMessageContent.setText("Leki :)");
                } else {
                    getInfoMedicinesTakenAndSetColor();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                toastMessage("Error: " + t.getMessage());
            }
        });
    }

    private void getPrediction() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalStorage.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Message email = new Message(GlobalStorage.email);

        Call<Message> call = jsonPlaceHolderApi.getPrediction(email);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (!response.isSuccessful()) {
                    toastMessage("Error code: " + response.code());
                    return;
                }
                Message message = response.body();
                String value = message.getText();
                if (value.equals("fail")) {
                    toastMessage("Nie udało się pobrać danych o przyszłym stanie zdrowia.");
                } else {
                    predictionTextView.setText(value);
//                    toastMessage(value);
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                toastMessage("Error: " + t.getMessage());
            }
        });
    }

    // INFO: Custom toast message
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
