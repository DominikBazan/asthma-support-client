package agh.asthmasupport.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import agh.asthmasupport.R;
import agh.asthmasupport.communication.JsonPlaceHolderApi;
import agh.asthmasupport.communication.objects.ChangeDosage;
import agh.asthmasupport.communication.objects.Medicine;
import agh.asthmasupport.communication.objects.MedicinesNamesToDelete;
import agh.asthmasupport.communication.objects.Message;
import agh.asthmasupport.communication.objects.NewMedicine;
import agh.asthmasupport.global.Encryption;
import agh.asthmasupport.global.GlobalStorage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MedicinesActivity extends AppCompatActivity {
    private ListView listView;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private ArrayAdapter<String> adapter;
    private Context context;
    private FloatingActionButton fabAddMedicines, fabRemoveMedicines;
    private ArrayList<String> itemList = new ArrayList<>();

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    private View addMedicinePopupView;
    private EditText newMedicineContent;
    private Button addMedicineButton, cancelAddMedicineButton;

    private TextView dosageLabel;
    private String newMedicine;
    private Button addDosageBtn, removeDosageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicines);

        dosageLabel = (TextView) findViewById(R.id.dosage_label);
        changeDosage(0);

        addDosageBtn = (Button) findViewById(R.id.add_dosage_btn);
        addDosageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDosage(2);
            }
        });

        removeDosageBtn = (Button) findViewById(R.id.remove_dosage_btn);
        removeDosageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDosage(1);
            }
        });

        listView = (ListView) findViewById(R.id.list_medicines);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalStorage.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        context = this;

        Message emailMessage = new Message(Encryption.encrypt(GlobalStorage.email));

        Call<ArrayList<Medicine>> call = jsonPlaceHolderApi.getMedicines(emailMessage);
        call.enqueue(new Callback<ArrayList<Medicine>>() {
            @Override
            public void onResponse(Call<ArrayList<Medicine>> call, Response<ArrayList<Medicine>> response) {
                if (!response.isSuccessful()) {
                    toastMessage("Error code: " + response.code());
                    return;
                }
                ArrayList<Medicine> medicines = response.body();
                Encryption.decryptArrayListOfMedicines(medicines);
                for (Medicine m : medicines) {
                    itemList.add(m.getMedicineName());
                }
                adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_multiple_choice, itemList);
                listView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<ArrayList<Medicine>> call, Throwable t) {
                toastMessage("Error: " + t.getMessage());
            }
        });

        fabAddMedicines = (FloatingActionButton) findViewById(R.id.add_medicine);
        fabAddMedicines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAddMedicineDialog();
            }
        });

        fabRemoveMedicines = (FloatingActionButton) findViewById(R.id.remove_medicine);
        fabRemoveMedicines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray positionChecker = listView.getCheckedItemPositions();
                ArrayList<String> medicinesToDelete = new ArrayList<String>();
                for (int m = listView.getCount() - 1; m >= 0; m--) {
                    if (positionChecker.get(m)) {
                        medicinesToDelete.add(adapter.getItem(m));
                        String rmFromList = adapter.getItem(m);
                        adapter.remove(itemList.get(m));
                        itemList.remove(rmFromList);
                    }
                }
                if (!medicinesToDelete.isEmpty()) {
                    deleteMedicinesFromList(medicinesToDelete);
                }
                positionChecker.clear();
                adapter.notifyDataSetChanged();
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

    // INFO: Add medicine dialog
    private void createAddMedicineDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        addMedicinePopupView = getLayoutInflater().inflate(R.layout.add_medicine_popup, null);
        newMedicineContent = (EditText) addMedicinePopupView.findViewById(R.id.new_medicine_content);
        addMedicineButton = (Button) addMedicinePopupView.findViewById(R.id.ok_add_medicine_button);
        addMedicineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newMedicine = newMedicineContent.getText().toString();
                if (newMedicine.isEmpty()) {
                    toastMessage("Podaj nazwę leku");
                    return;
                }
                if (itemList.contains(newMedicine)) {
                    dialog.dismiss();
                    return;
                }
                NewMedicine newMed = new NewMedicine(Encryption.encrypt(newMedicine), Encryption.encrypt(GlobalStorage.email));
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(GlobalStorage.baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
                Call<Message> call = jsonPlaceHolderApi.getMedicines(newMed);
                call.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        if (!response.isSuccessful()) {
                            toastMessage("Error code: " + response.code());
                            return;
                        }
                        // Message messages = response.body();
                        // String m = Encryption.decrypt(messages.getText());
                        dialog.dismiss();
                        itemList.add(newMedicine);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {
                        toastMessage("Error: " + t.getMessage());
                    }
                });
            }
        });

        cancelAddMedicineButton = (Button) addMedicinePopupView.findViewById(R.id.cancel_add_medicine_button);
        cancelAddMedicineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogBuilder.setView(addMedicinePopupView);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    private void deleteMedicinesFromList(ArrayList<String> medicinesToDelete) {
        MedicinesNamesToDelete medicinesNamesToDelete = new MedicinesNamesToDelete(Encryption.encrypt(GlobalStorage.email), medicinesToDelete);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalStorage.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<Message> call = jsonPlaceHolderApi.deleteMedicinesUsed(medicinesNamesToDelete);
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

    private void changeDosage(int mode) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalStorage.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        ChangeDosage changeDosage = new ChangeDosage(Encryption.encrypt(GlobalStorage.email), Encryption.encrypt(Integer.toString(mode)));

        Call<Message> call = jsonPlaceHolderApi.changeDosage(changeDosage);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (!response.isSuccessful()) {
                    toastMessage("Error code: " + response.code());
                    return;
                }
                Message message = response.body();
                String m = Encryption.decrypt(message.getText());
                if (m.equals("ERROR")) {
                    dosageLabel.setText("Liczba dawek: -");
                } else {
                    dosageLabel.setText("Liczba dawek: " + m);
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
