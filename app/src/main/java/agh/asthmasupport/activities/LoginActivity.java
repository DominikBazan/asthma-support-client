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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
////        TODO
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.server_settings_menu, menu);
//
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
////        TODO
//        switch (item.getItemId()){
//            case R.id.logout_option:
//                //TODO
////                Intent serverSettingsIntent = new Intent(LoginActivity.this, SettingsServerActivity.class);
////                startActivity(serverSettingsIntent);
//                toastMessage("OK: se_se_item1 clicked.");
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginNameButton = (Button) findViewById(R.id.login);
        registerButton = (Button) findViewById(R.id.register);
        loginName_textField = (EditText) findViewById(R.id.username);
        password_textField = (EditText) findViewById(R.id.password);

        // TODO: temporary input
        loginName_textField.setText("user17@gmail.com");
        password_textField.setText("password");

        loginNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalStorage.email = loginName_textField.getText().toString();
                GlobalStorage.password = password_textField.getText().toString();

                try {
                    login();
                } catch (Exception ex) {
                    toastMessage("Wystąpił problem z logowaniem.");
                }
             }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    GlobalStorage.email = loginName_textField.getText().toString();
                    GlobalStorage.password = password_textField.getText().toString();

                    try {
                        register();
                    } catch (Exception ex) {
                        toastMessage("Wystąpił problem z rejestracją.");
                    }
                } catch (Exception ex) {
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

        toastMessage("Logowanie . . .");
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
                    toastMessage("Zalogowano");
                    Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                    startActivity(intent);
                } else {
                    toastMessage("Złe hasło.");
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                toastMessage("Error: " + t.getMessage());
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
        UserCredentials userCredentials = new UserCredentials(email, encryptedPassword);
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
                } else if (m1.equals("Failure")) {
                    //TODO Internet connection
                    toastMessage("Failure. Try again or contact administrator.");
                } else {
                    toastMessage("Unknown error");
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                toastMessage("Error: " + t.getMessage());
            }
        });
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
