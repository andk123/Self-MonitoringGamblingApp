package com.example.sebastiena.selfmonitoringgamblingapplication;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import FireBase.DatabaseHelper;
import Objects.GamblingSessionEntity;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GamblingSessionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GamblingSessionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GamblingSessionFragment extends Fragment implements TimePickerDialog.OnTimeSetListener {
    // TODO: Rename parameter arguments, choose names that match

    int endHour, endMin, startHour, startMin;
    boolean isStart;
    private DatabaseReference mDatabase;
    private ProgressBar progressBar;
    boolean isUpdate = false;
    TextView timeStarts = null;
    TextView timeEnds = null;


    private OnFragmentInteractionListener mListener;

    public GamblingSessionFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static GamblingSessionFragment newInstance() {
        GamblingSessionFragment fragment = new GamblingSessionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        final ViewGroup view = (ViewGroup)inflater.inflate(R.layout.fragment_gambling_session, container, false);
        final Spinner s1 = (Spinner) view.findViewById(R.id.spinner_mode);
        final Spinner s2 = (Spinner) view.findViewById(R.id.spinner_game);
        final EditText startingAmount = (EditText) view.findViewById(R.id.starting_amount);
        final EditText finalAmount = (EditText) view.findViewById(R.id.final_amount);
        final Button buttonSubmit = (Button) view.findViewById(R.id.submit_gs);
        final Button startButton = (Button) view.findViewById(R.id.start_time);
        final Button endButton = (Button) view.findViewById(R.id.end_time);
        timeStarts = (TextView) view.findViewById(R.id.start_time_text);
        timeEnds = (TextView) view.findViewById(R.id.end_time_text);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        // Spinner Drop down elements
        List<String> modes = new ArrayList<String>();
        modes.add("Select a Game Mode");
        modes.add("Online");
        modes.add("Offline");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(super.getActivity(), android.R.layout.simple_spinner_item, modes);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        s1.setAdapter(dataAdapter);
        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);

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
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(super.getContext(), android.R.layout.simple_spinner_item, games);

        // Drop down layout style - list view with radio button
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        s2.setAdapter(dataAdapter2);
        s2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub

            }
        });



        final GamblingSessionEntity passedEntity =  (GamblingSessionEntity) super.getActivity().getIntent().getSerializableExtra("GamblingSession");
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
                isStart = true;
                showTimePickerDialog(view);
            }
        });

        endButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                isStart = false;
                showTimePickerDialog(view);
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
                    Toast.makeText(GamblingSessionFragment.super.getContext(), "Enter Starting Amount!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(fAmount)) {
                    Toast.makeText(GamblingSessionFragment.super.getContext(), "Enter Final Amount!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mode.equals("Select a Game Mode")) {
                    Toast.makeText(GamblingSessionFragment.super.getContext(), "Choose a Game Mode", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (game.equals("Select a Game Type")) {
                    Toast.makeText(GamblingSessionFragment.super.getContext(), "Choose a Game Type", Toast.LENGTH_SHORT).show();
                    return;

                }

                if (startHour == 0 && startMin == 0 && !isUpdate) {
                    Toast.makeText(GamblingSessionFragment.super.getContext(), "Set start Time", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (endHour == 0 && endMin == 0  && !isUpdate) {
                    Toast.makeText(GamblingSessionFragment.super.getContext(), "Set end Time", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(GamblingSessionFragment.super.getContext(), "Gambling Session Updated", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(GamblingSessionFragment.super.getContext(), newMainActivity.class);
                        intent.putExtra("Unique","FromGs");
                        startActivity(intent);


                    } else {
                        Toast.makeText(GamblingSessionFragment.super.getContext(), "Gambling Session Update resulted in Error", Toast.LENGTH_LONG).show();
                        return;

                    }



                }else{
                    GamblingSessionEntity gsToSave = createGamblingSessionEntity(sAmount, fAmount, mode, game, duration, stringDate);

                    if (dbHelper.saveGamblingSession(gsToSave)) {
                        Toast.makeText(GamblingSessionFragment.super.getContext(), "Gambling Session Saved", Toast.LENGTH_LONG).show();


                        Intent intent = new Intent(GamblingSessionFragment.super.getContext(), newMainActivity.class);
                        intent.putExtra("Unique","FromGs");
                        startActivity(intent);



                    } else {
                        Toast.makeText(GamblingSessionFragment.super.getContext(), "Gambling Session save resulted in Error", Toast.LENGTH_LONG).show();
                        return;

                    }



                }


            }
        });



        return view;
    }




    public GamblingSessionEntity createGamblingSessionEntity(String startingAmount, String finalAmount, String mode, String game, int duration, String stringDate) {
        String startTime = startHour + ":" + startMin;
        String endTime = endHour + ":" + endMin;
        int outcome = Integer.parseInt(finalAmount) - Integer.parseInt(startingAmount);
        String stringOutcome = Integer.toString(outcome);
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
        //Make sure we have no negative duration
        if (difHour < 0){
            difHour = 24 + difHour;
        }
        int difMin = endMin - startMin;

        return difHour * 60 + difMin;
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(super.getChildFragmentManager(), "timePicker");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        if (isStart) {
            String dayRange = "am";
            startHour = hourOfDay;
            startMin = minute;
            if (startHour > 11) {
                dayRange = "pm";
                startHour = (startHour == 12) ? 12 : startHour - 12;
            }

            timeStarts.setText(startHour + ":" + startMin + dayRange);
        } else {
            String dayRange = "am";
            endHour = hourOfDay;
            endMin = minute;
            if (endHour > 11) {
                dayRange = "pm";
                endHour = (endHour == 12) ? 12 : endHour - 12;
            }
            timeEnds.setText(endHour + ":" + endMin + dayRange);

        }

    }






    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
