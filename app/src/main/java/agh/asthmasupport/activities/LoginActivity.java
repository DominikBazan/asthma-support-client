package agh.asthmasupport.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import agh.asthmasupport.R;
import agh.asthmasupport.communication.JsonPlaceHolderApi;
import agh.asthmasupport.communication.objects.Message;
import agh.asthmasupport.communication.objects.UserCredentials;
import agh.asthmasupport.global.BCryptOperations;
import agh.asthmasupport.global.Encryption;
import agh.asthmasupport.global.GlobalStorage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private Button loginNameButton, registerButton;
    private EditText loginName_textField, password_textField;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    int atempt;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginNameButton = (Button) findViewById(R.id.login);
        registerButton = (Button) findViewById(R.id.register);
        loginName_textField = (EditText) findViewById(R.id.username);
        password_textField = (EditText) findViewById(R.id.password);

        // DONE: temporary input
//        loginName_textField.setText("dominik@gmail.com");
//        password_textField.setText("password");

        loginNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalStorage.email = loginName_textField.getText().toString();
                GlobalStorage.password = password_textField.getText().toString();

                try {
                    atempt = 1;
                    login();
                } catch (Exception ex) {
                    toastMessage("Wystąpił problem z logowaniem.");
                }
            }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalStorage.email = loginName_textField.getText().toString();
                GlobalStorage.password = password_textField.getText().toString();

                try {
                    atempt = 1;
                    register();
                } catch (Exception ex) {
                    toastMessage("Wystąpił problem z rejestracją.");
                }

            }
        });
    }

    private void login() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalStorage.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Message message = new Message(GlobalStorage.email);

        int d = 150;
        if (atempt % d == 0) {
            String s = "Logowanie ";
            for (int i = atempt/d; i > 1; i--) {
                s += ". ";
            }
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
            toast.show();
        }

        Call<List<Message>> call = jsonPlaceHolderApi.getPasswordHash(message);
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (!response.isSuccessful()) {
                    toastMessage("Error code: " + response.code());
                    return;
                }

                List<Message> messages = response.body();
                String hash1 = messages.get(0).getText();

                if (hash1.equals("")) {
                    toastMessage("Nie ma takiego użytkownika.");
                } else if (BCryptOperations.isValid(GlobalStorage.password, hash1)) {
                    addMissingMedicineEventsAndTestsRequest(GlobalStorage.email);
                    Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                    startActivity(intent);
                } else {
                    toastMessage("Złe hasło.");
                }
                atempt = 1;
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                if (atempt < 4000) {
                    atempt += 1;
//                    Toast.makeText(getApplicationContext(), atempt, Toast.LENGTH_SHORT).show();
                    login();
                } else {
                    toastMessage("Wystąpił błąd. Sprawdź połączenie z internetem.");
                    if (toast != null) {
                        toast.cancel();
                    }
                }

            }
        });

    }

    private void register() {
        String email = GlobalStorage.email;
        String password = GlobalStorage.password;
        if (email.isEmpty()) {
            toastMessage("Wpisz email");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            toastMessage("Niepoprawny adres email");
            return;
        }
        if (password.isEmpty()) {
            toastMessage("Podaj hasło");
            return;
        }
        if (password.length() < 6) {
            toastMessage("Hasło powinno zawierać przynamniej 6 znaków");
            return;
        }
        String encryptedPassword = BCryptOperations.generateHashedPass(password);
        UserCredentials userCredentials = new UserCredentials(Encryption.encrypt(email), encryptedPassword);

        Toast.makeText(getApplicationContext(), "Rejestracja", Toast.LENGTH_SHORT).show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalStorage.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<List<Message>> call = jsonPlaceHolderApi.registerUser(userCredentials);
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (!response.isSuccessful()) {
                    toastMessage("Error code: " + response.code());
                    return;
                }
                List<Message> messages = response.body();
                String m1 = messages.get(0).getText();
                if (m1.equals("Taken")) {
                    toastMessage("Somebody is already using this email");
                } else if (m1.equals("Success")) {
                    toastMessage("Success. You can log in now.");
                    addFirstMedicineEventAndTestRequest(GlobalStorage.email);
                } else if (m1.equals("Failure")) {
                    toastMessage("Failure. Try again or contact administrator.");
                } else {
                    toastMessage("Unknown error");
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                if (atempt < 4) {
                    atempt += 1;
//                    Toast.makeText(getApplicationContext(), atempt, Toast.LENGTH_SHORT).show();
                    register();
                } else {
                    toastMessage("Wystąpił błąd. Sprawdź połączenie z internetem.");
                    if (toast != null) {
                        toast.cancel();
                    }
                }
            }
        });
    }

    private void addMissingMedicineEventsAndTestsRequest(String email) {
        Message emailMessage = new Message(email);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalStorage.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<Message> call = jsonPlaceHolderApi.addMissingMedicineEventsAndTests(emailMessage);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (!response.isSuccessful()) {
                    toastMessage("Error code: " + response.code());
                    return;
                }
                Message messages = response.body();
                String m = messages.getText();
                // toastMessage(m);
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                toastMessage("Error: " + t.getMessage());
            }
        });
    }

    private void addFirstMedicineEventAndTestRequest(String email) {
        Message emailMessage = new Message(email);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalStorage.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<Message> call = jsonPlaceHolderApi.addFirstMedicineEventAndTest(emailMessage);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (!response.isSuccessful()) {
                    toastMessage("Error code: " + response.code());
                    return;
                }
//                Message messages = response.body();
//                String m = messages.getText();
//                toastMessage(m);
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                toastMessage("Error: " + t.getMessage());
            }
        });
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
