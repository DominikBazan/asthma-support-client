package agh.asthmasupport.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import agh.asthmasupport.R;
import agh.asthmasupport.communication.JsonPlaceHolderApi;
import agh.asthmasupport.communication.objects.AllergiesNamesToDelete;
import agh.asthmasupport.communication.objects.Message;
import agh.asthmasupport.communication.objects.NewAllergy;
import agh.asthmasupport.global.Encryption;
import agh.asthmasupport.global.GlobalStorage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AllergiesActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ListView listView;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private ArrayAdapter<String> adapterAllergies;
    private Context context;
    private FloatingActionButton fabAddAllergy, fabRemoveAllergies;
    private ArrayList<String> itemList = new ArrayList<String>();

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    private View addAllergyPopupView;
    private Spinner allergiesChoiceSpinner;
    private ArrayAdapter<String> adapterChoseAllergies;
    private ArrayList<String> asthmaFactorsNames = new ArrayList<String>();
    private String chosenAllergy;
    private Button addAllergyButton, cancelAddAllergyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alelrgies);

        listView = (ListView) findViewById(R.id.list_allergies);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalStorage.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        context = this;

        Message emailMessage = new Message(Encryption.encrypt(GlobalStorage.email));

        Call<ArrayList<Message>> call = jsonPlaceHolderApi.getUsersAllergies(emailMessage);
        call.enqueue(new Callback<ArrayList<Message>>() {
            @Override
            public void onResponse(Call<ArrayList<Message>> call, Response<ArrayList<Message>> response) {
                if (!response.isSuccessful()) {
                    toastMessage("Error code: " + response.code());
                    return;
                }
                ArrayList<Message> allergies = response.body();
                Encryption.decryptArrayListOfMessages(allergies);
                for (Message m : allergies) {
                    itemList.add(m.getText());
                }
                adapterAllergies = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_multiple_choice, itemList);
                listView.setAdapter(adapterAllergies);
            }

            @Override
            public void onFailure(Call<ArrayList<Message>> call, Throwable t) {
                toastMessage("Error: " + t.getMessage());
            }
        });

        fabAddAllergy = (FloatingActionButton) findViewById(R.id.add_allergy);
        fabAddAllergy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAddAllergyDialog();
            }
        });

        fabRemoveAllergies = (FloatingActionButton) findViewById(R.id.remove_allergy);
        fabRemoveAllergies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray positionChecker = listView.getCheckedItemPositions();
                ArrayList<String> allergiesToDelete = new ArrayList<String>();
                for (int a = listView.getCount() - 1; a >= 0; a--) {
                    if (positionChecker.get(a)) {
                        allergiesToDelete.add(adapterAllergies.getItem(a));
                        adapterAllergies.remove(itemList.get(a));
                    }
                }
                if (!allergiesToDelete.isEmpty()) {
                    deleteAllergiesFromList(allergiesToDelete);
                }
                positionChecker.clear();
                adapterAllergies.notifyDataSetChanged();
            }
        });

        getAllAsthmaFactorsNames();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    // INFO: Add allergy dialog
    private void createAddAllergyDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        addAllergyPopupView = getLayoutInflater().inflate(R.layout.add_allergy_popup, null);

        allergiesChoiceSpinner = (Spinner) addAllergyPopupView.findViewById(R.id.allergies_choice_spinner);
        adapterChoseAllergies = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, asthmaFactorsNames);
        adapterAllergies.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        allergiesChoiceSpinner.setAdapter(adapterChoseAllergies);
        allergiesChoiceSpinner.setOnItemSelectedListener(this);

        addAllergyButton = (Button) addAllergyPopupView.findViewById(R.id.ok_add_allergy_button);
        addAllergyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chosenAllergy.isEmpty()) {
                    toastMessage("Podaj nazwę alergii");
                    return;
                }
                if (itemList.contains(chosenAllergy)) {
                    dialog.dismiss();
                    return;
                }
                NewAllergy newAll = new NewAllergy(Encryption.encrypt(chosenAllergy), Encryption.encrypt(GlobalStorage.email));
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(GlobalStorage.baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
                Call<Message> call = jsonPlaceHolderApi.addNewAllergy(newAll);
                call.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        if (!response.isSuccessful()) {
                            toastMessage("Error code: " + response.code());
                            return;
                        }
                        dialog.dismiss();
                        itemList.add(chosenAllergy);
                        adapterAllergies.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {
                        toastMessage("Error: " + t.getMessage());
                    }
                });
            }
        });

        cancelAddAllergyButton = (Button) addAllergyPopupView.findViewById(R.id.cancel_add_allergy_button);
        cancelAddAllergyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogBuilder.setView(addAllergyPopupView);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    // INFO: it's about spinner for allergy types
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chosenAllergy = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void getAllAsthmaFactorsNames() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalStorage.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        context = this;

        Message emailMessage = new Message(Encryption.encrypt(GlobalStorage.email));

        Call<ArrayList<Message>> call = jsonPlaceHolderApi.getAllAsthmaFactors(emailMessage);
        call.enqueue(new Callback<ArrayList<Message>>() {
            @Override
            public void onResponse(Call<ArrayList<Message>> call, Response<ArrayList<Message>> response) {
                if (!response.isSuccessful()) {
                    toastMessage("Error code: " + response.code());
                    return;
                }
                ArrayList<Message> asthmaFactors = response.body();
                asthmaFactorsNames.clear();
                for (Message a : asthmaFactors) {
                    asthmaFactorsNames.add(a.getText());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Message>> call, Throwable t) {
                toastMessage("Error: " + t.getMessage());
            }
        });
    }

    private void deleteAllergiesFromList(ArrayList<String> allergiesToDelete) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalStorage.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        AllergiesNamesToDelete allergiesNamesToDelete = new AllergiesNamesToDelete(Encryption.encrypt(GlobalStorage.email), allergiesToDelete);

        Call<Message> call = jsonPlaceHolderApi.deleteAllergiesUsed(allergiesNamesToDelete);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (!response.isSuccessful()) {
                    toastMessage("Error code: " + response.code());
                    return;
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
