package com.example.sebastiena.selfmonitoringgamblingapplication;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import FireBase.DatabaseHelper;
import Objects.GamblingSessionEntity;

public class GamblingSessionActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    int endHour, endMin, startHour, startMin;
    boolean isStart;
    private DatabaseReference mDatabase;
    private ProgressBar progressBar;
    boolean isUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gambling_session);
        final Spinner s1 = (Spinner) findViewById(R.id.spinner_mode);
        final Spinner s2 = (Spinner) findViewById(R.id.spinner_game);
        final EditText startingAmount = (EditText) findViewById(R.id.starting_amount);
        final EditText finalAmount = (EditText) findViewById(R.id.final_amount);
        final Button buttonSubmit = (Button) findViewById(R.id.submit_gs);
        final Button startButton = (Button) findViewById(R.id.start_time);
        final Button endButton = (Button) findViewById(R.id.end_time);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Spinner Drop down elements
        List<String> modes = new ArrayList<String>();
        modes.add("Select a Game Mode");
        modes.add("Online");
        modes.add("Offline");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, modes);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        s1.setAdapter(dataAdapter);
        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                if (item != null) {
                    Toast.makeText(GamblingSessionActivity.this, item.toString(),
                            Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(GamblingSessionActivity.this, "Selected",
                        Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub

            }
        });


        // Spinner Drop down elements
        List<String> games = new ArrayList<String>();
        games.add("Select a Game Type");
        games.add("Poker");
        games.add("Blackjack");
        games.add("Craps");
        games.add("Roulette");
        games.add("Slots");
        games.add("Sports wagering");
        games.add("Lottery tickets/scratch cards");
        games.add("Other");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, games);

        // Drop down layout style - list view with radio button
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        s2.setAdapter(dataAdapter2);
        s2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                if (item != null) {
                    Toast.makeText(GamblingSessionActivity.this, item.toString(),
                            Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(GamblingSessionActivity.this, "Selected",
                        Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub

            }
        });




        final GamblingSessionEntity passedEntity = (GamblingSessionEntity) getIntent().getSerializableExtra("GamblingSession");
        if (passedEntity != null) {
            isUpdate = true;
            startingAmount.setText(passedEntity.getStartingAmount());
            finalAmount.setText(passedEntity.getFinalAmount());
            int position = dataAdapter.getPosition(passedEntity.getMode());
            s1.setSelection(position);
            int position2 = dataAdapter2.getPosition(passedEntity.getGame());
            s2.setSelection(position2);
            buttonSubmit.setText("Update Session");


        }


        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                View currentView = findViewById(android.R.id.content);
                isStart = true;
                showTimePickerDialog(currentView);
            }
        });

        endButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                View currentView = findViewById(android.R.id.content);
                isStart = false;
                showTimePickerDialog(currentView);
            }
        });


        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                String sAmount = startingAmount.getText().toString().trim();
                String fAmount = finalAmount.getText().toString().trim();
                String mode = s1.getSelectedItem().toString().trim();
                String game = s2.getSelectedItem().toString().trim();
                if (TextUtils.isEmpty(sAmount)) {
                    Toast.makeText(getApplicationContext(), "Enter Starting Amount!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(fAmount)) {
                    Toast.makeText(getApplicationContext(), "Enter Final Amount!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mode.equals("Select a Game Mode")) {
                    Toast.makeText(getApplicationContext(), "Choose a Game Mode", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (game.equals("Select a Game Type")) {
                    Toast.makeText(getApplicationContext(), "Choose a Game Type", Toast.LENGTH_SHORT).show();
                    return;

                }

                if (startHour == 0 && startMin == 0 && !isUpdate) {
                    Toast.makeText(getApplicationContext(), "Set start Time", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (endHour == 0 && endMin == 0  && !isUpdate) {
                    Toast.makeText(getApplicationContext(), "Set end Time", Toast.LENGTH_SHORT).show();
                    return;
                }

                int duration = calcDifTime();
                String stringDate = getDate();


                DatabaseHelper dbHelper = new DatabaseHelper(FirebaseDatabase.getInstance().getReference());

                if (isUpdate){
                    if(startHour == 0 && startMin ==0 & endHour == 0 && endMin == 0 ){
                        duration = passedEntity.getDuration();
                    } else{
                        String startTime = startHour + ":" + startMin;
                        String endTime = endHour + ":" + endMin;
                        passedEntity.setStartTime(startTime);
                        passedEntity.setEndTime(endTime);
                    }
                    passedEntity.setDuration(duration);
                    passedEntity.setGame(game);
                    passedEntity.setMode(mode);
                    passedEntity.setStartingAmount(sAmount);
                    passedEntity.setFinalAmount(fAmount);
                    if(dbHelper.updateGamblingSession(passedEntity)){
                        Toast.makeText(GamblingSessionActivity.this, "Gambling Session Updated", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(GamblingSessionActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(GamblingSessionActivity.this, "Gambling Session Update resulted in Error", Toast.LENGTH_LONG).show();
                        return;

                    }



                }else{
                    GamblingSessionEntity gsToSave = createGamblingSessionEntity(sAmount, fAmount, mode, game, duration, stringDate);

                    if (dbHelper.saveGamblingSession(gsToSave)) {
                        Toast.makeText(GamblingSessionActivity.this, "Gambling Session Saved", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(GamblingSessionActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(GamblingSessionActivity.this, "Gambling Session save resulted in Error", Toast.LENGTH_LONG).show();
                        return;

                    }



                }


            }
        });


    }


    public GamblingSessionEntity createGamblingSessionEntity(String startingAmount, String finalAmount, String mode, String game, int duration, String stringDate) {
        String startTime = startHour + ":" + startMin;
        String endTime = endHour + ":" + endMin;
        int outcome = Integer.parseInt(finalAmount) + Integer.parseInt(startingAmount);
        String stringOutcome = Integer.toHexString(outcome);
        GamblingSessionEntity gs = new GamblingSessionEntity(FirebaseAuth.getInstance().getCurrentUser().getUid(), FirebaseAuth.getInstance().getCurrentUser().getEmail(), stringDate, mode, game, startingAmount, finalAmount, stringOutcome, startTime, endTime, duration);
        return gs;
    }

    public String getDate() {
        Date date = new Date();
        Date newDate = new Date(date.getTime() + (604800000L * 2) + (24 * 60 * 60));
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        return dt.format(newDate);

    }

    public int calcDifTime() {
        int difHour = endHour - startHour;
        int difMin = endMin - startMin;

        return difHour * 60 + difMin;
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        if (isStart) {
            startHour = hourOfDay;
            startMin = minute;
        } else {
            endHour = hourOfDay;
            endMin = minute;

        }

    }


}
