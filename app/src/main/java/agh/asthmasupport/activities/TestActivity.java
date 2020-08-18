package agh.asthmasupport.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import agh.asthmasupport.R;
import agh.asthmasupport.communication.JsonPlaceHolderApi;
import agh.asthmasupport.communication.objects.Message;
import agh.asthmasupport.communication.objects.TestResult;
import agh.asthmasupport.communication.objects.UserCredentials;
import agh.asthmasupport.global.GlobalStorage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TestActivity extends AppCompatActivity {

    private JsonPlaceHolderApi jsonPlaceHolderApi;

    TextView counter, question;
    RadioGroup radioGroup;
    RadioButton o1, o2, o3, o4, o5;
    Button confirmButton;

    int questionIterator;
    int pointsCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        counter = (TextView) findViewById(R.id.counter);
        question = (TextView) findViewById(R.id.question);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        o1 = (RadioButton) findViewById(R.id.o1);
        o2 = (RadioButton) findViewById(R.id.o2);
        o3 = (RadioButton) findViewById(R.id.o3);
        o4 = (RadioButton) findViewById(R.id.o4);
        o5 = (RadioButton) findViewById(R.id.o5);
        confirmButton = (Button) findViewById(R.id.confirm_button);

        confirmButton.setEnabled(false);
        questionIterator = 1;
        pointsCounter = 0;
        fillInTheQuestionsAndAnswers(questionIterator);
        updateCounter(questionIterator);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (group.getCheckedRadioButtonId() != -1) {
                    confirmButton.setEnabled(true);
                }
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (o1.isActivated()) pointsCounter += 1;
                else if (o2.isActivated()) pointsCounter += 2;
                else if (o2.isActivated()) pointsCounter += 3;
                else if (o2.isActivated()) pointsCounter += 4;
                else pointsCounter += 5;

                if (questionIterator == 5) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    TestResult testResult = new TestResult(GlobalStorage.email, Integer.toString(pointsCounter),  df.format(new Date()));
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(GlobalStorage.baseUrl)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
                    Call<List<Message>> call = jsonPlaceHolderApi.addTestResult(testResult);
                    call.enqueue(new Callback<List<Message>>() {
                        @Override
                        public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                            if (!response.isSuccessful()) {
                                toastMessage("Error code: " + response.code());
                                return;
                            }

                            List<Message> messages = response.body();
                            String m1 = messages.get(0).getText();

                            if (m1.equals("Zapisano wynik")) {
                                toastMessage("Wyniki zapisane");
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

                    Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    return;
                }

                questionIterator++;
                fillInTheQuestionsAndAnswers(questionIterator);

                radioGroup.clearCheck();

                confirmButton.setEnabled(false);

                updateCounter(questionIterator);
            }
        });

    }

    private void updateCounter(int questionIterator) {
        counter.setText("Pytanie " + questionIterator + " / 5");
    }

    private void fillInTheQuestionsAndAnswers(int questionIterator) {
        switch (questionIterator) {
            case 1:
                question.setText(getText(R.string.p1));
                o1.setText(getText(R.string.o11));
                o2.setText(getText(R.string.o12));
                o3.setText(getText(R.string.o13));
                o4.setText(getText(R.string.o14));
                o5.setText(getText(R.string.o15));
                break;
            case 2:
                question.setText(getText(R.string.p2));
                o1.setText(getText(R.string.o21));
                o2.setText(getText(R.string.o22));
                o3.setText(getText(R.string.o23));
                o4.setText(getText(R.string.o24));
                o5.setText(getText(R.string.o25));
                break;
            case 3:
                question.setText(getText(R.string.p3));
                o1.setText(getText(R.string.o31));
                o2.setText(getText(R.string.o32));
                o3.setText(getText(R.string.o33));
                o4.setText(getText(R.string.o34));
                o5.setText(getText(R.string.o35));
                break;
            case 4:
                question.setText(getText(R.string.p4));
                o1.setText(getText(R.string.o41));
                o2.setText(getText(R.string.o42));
                o3.setText(getText(R.string.o43));
                o4.setText(getText(R.string.o44));
                o5.setText(getText(R.string.o45));
                break;
            case 5:
                question.setText(getText(R.string.p5));
                o1.setText(getText(R.string.o51));
                o2.setText(getText(R.string.o52));
                o3.setText(getText(R.string.o53));
                o4.setText(getText(R.string.o54));
                o5.setText(getText(R.string.o55));
                break;
        }

    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
