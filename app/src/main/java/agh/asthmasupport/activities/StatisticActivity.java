package agh.asthmasupport.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import agh.asthmasupport.Lists.DailyStatsAdapter;
import agh.asthmasupport.R;
import agh.asthmasupport.communication.JsonPlaceHolderApi;
import agh.asthmasupport.communication.objects.DailyStatistics;
import agh.asthmasupport.communication.objects.Message;
import agh.asthmasupport.global.Encryption;
import agh.asthmasupport.global.GlobalStorage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StatisticActivity extends AppCompatActivity {

    private ListView listView;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private ArrayList<DailyStatistics> dailyStats;
    private Context context;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialogSendEmail;

    private View sendEmailPopupView;
    private EditText emailContent;
    private Button sendButton, cancelButton;

    private String emailTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        listView = (ListView) findViewById(R.id.list_stat);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalStorage.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        context = this;

        Message emailMessage = new Message(Encryption.encrypt(GlobalStorage.email));

        Call<ArrayList<DailyStatistics>> call = jsonPlaceHolderApi.getStatistics(emailMessage);
        call.enqueue(new Callback<ArrayList<DailyStatistics>>() {
            @Override
            public void onResponse(Call<ArrayList<DailyStatistics>> call, Response<ArrayList<DailyStatistics>> response) {
                if (!response.isSuccessful()) {
                    toastMessage("Error code: " + response.code());
                    return;
                }
                ArrayList<DailyStatistics> dailyStats = response.body();
                Encryption.decryptArrayListOfDailyStatistics(dailyStats);
                for (DailyStatistics ds : dailyStats) {
                    if (ds.getImplemented().equals("1")) ds.setImplemented("TAK");
                    else if (ds.getImplemented().equals("0")) ds.setImplemented("NIE");
                }
                DailyStatsAdapter dailyStatsAdapter = new DailyStatsAdapter(context, R.layout.adapter_view_layout_stats, dailyStats);
                listView.setAdapter(dailyStatsAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<DailyStatistics>> call, Throwable t) {
                toastMessage("Error: " + t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    //INFO: Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send_statistics_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.statistics_send_email:
                createSendEmailDialog();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // INFO: Send email
    public void createSendEmailDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        sendEmailPopupView = getLayoutInflater().inflate(R.layout.send_email_popup, null);
        emailContent = (EditText) sendEmailPopupView.findViewById(R.id.email_content);
        //DONE: temporary input
//        emailContent.setText("dominik.f.bazan@gmail.com");
        sendButton = (Button) sendEmailPopupView.findViewById(R.id.ok_send_email_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailTo = emailContent.getText().toString();
                if (emailTo.isEmpty()) {
                    toastMessage("Wprowadź adres email");
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(emailTo).matches()) {
                    toastMessage("Niepoprawny adres email");
                    return;
                }

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(GlobalStorage.baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

                final Message emailMessage = new Message(Encryption.encrypt(GlobalStorage.email));

                Call<ArrayList<DailyStatistics>> call = jsonPlaceHolderApi.getStatistics(emailMessage);
                call.enqueue(new Callback<ArrayList<DailyStatistics>>() {
                    @Override
                    public void onResponse(Call<ArrayList<DailyStatistics>> call, Response<ArrayList<DailyStatistics>> response) {
                        if (!response.isSuccessful()) {
                            toastMessage("Error code: " + response.code());
                            return;
                        }
                        ArrayList<DailyStatistics> dailyStats = response.body();
                        Encryption.decryptArrayListOfDailyStatistics(dailyStats);
                        StringBuilder sb = new StringBuilder();
                        for (DailyStatistics ds : dailyStats) {
                            sb.append("Data: " + ds.getDate()+"\t");
                            sb.append("Pkt.: " + ds.getValue()+"\t");
                            if (ds.getImplemented().equals("1")) sb.append("Leki: TAK\t");
                            else if (ds.getImplemented().equals("0")) sb.append("Leki: NIE\t");
                            sb.append("Deszcz: " + ds.getRain()+" mm\t");
                            sb.append("Wiatr: " + ds.getWind()+" m/s\t");
                            sb.append("Temp.: " + ds.getTemperature()+" st.C\t");
                            String dusting = ds.getDusting();
                            dusting = dusting.replaceAll("\n", ", ");
                            sb.append("Pylenie: " + dusting +"\t\n\n");
                        }

                        sendEmail(emailTo, sb.toString());

                    }

                    @Override
                    public void onFailure(Call<ArrayList<DailyStatistics>> call, Throwable t) {
                        toastMessage("Error: " + t.getMessage());
                    }
                });
            }
        });

        cancelButton = (Button) sendEmailPopupView.findViewById(R.id.cancel_send_email_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSendEmail.dismiss();
            }
        });

        dialogBuilder.setView(sendEmailPopupView);
        dialogSendEmail = dialogBuilder.create();
        dialogSendEmail.show();
    }

    private void sendEmail(String email, String data) {
        String message = "Dane o przebiegu astmy zebrane za pomocą systemu '" + getResources().getString(R.string.systemName_tx) + "' użytkownika o adresie email " + GlobalStorage.email + ".\n\n\n";;

        String[] to = {email};

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, to);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Pomiary użytkownika '" + GlobalStorage.email + "' .");
        intent.putExtra(Intent.EXTRA_TEXT, message + data);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Wybierz klienta poczty"));
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
