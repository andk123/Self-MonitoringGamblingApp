package com.example.sebastiena.selfmonitoringgamblingapplication;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import FireBase.DatabaseHelper;
import Objects.GamblingSessionEntity;
import Objects.UserEntity;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        final ViewGroup view = (ViewGroup)inflater.inflate(R.layout.fragment_settings, container, false);
        final Spinner s1 = (Spinner) view.findViewById(R.id.spinner_sex);
        final EditText year = (EditText) view.findViewById(R.id.yearofbirth);
        final Button updatebutton = (Button) view.findViewById(R.id.updateprofile);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        // Spinner Drop down elements
        List<String> sex = new ArrayList<String>();
        sex.add("Sex");
        sex.add("Male");
        sex.add("Female");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(super.getActivity(), android.R.layout.simple_spinner_item, sex);

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
        final List<String> notifications = new ArrayList<String>();
        notifications.add("Notifications");
        notifications.add("Yes");
        notifications.add("No");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(super.getContext(), android.R.layout.simple_spinner_item, notifications);



        updatebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                String yob = year.getText().toString().trim();
                String spinsex = s1.getSelectedItem().toString().trim();
                if (spinsex.equals("Sex")) {
                    spinsex = null;
                    return;

                }
                DatabaseHelper dbHelper = new DatabaseHelper(FirebaseDatabase.getInstance().getReference());
                if(dbHelper.updateUserEntity(FirebaseAuth.getInstance().getCurrentUser().getUid(),yob,spinsex)){
                    Toast.makeText(SettingsFragment.super.getContext(), "User Settings Updated", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SettingsFragment.super.getContext(), newMainActivity.class);
                    startActivity(intent);


                } else {
                    Toast.makeText(SettingsFragment.super.getContext(), "User Settings Update resulted in Error", Toast.LENGTH_LONG).show();
                    return;

                }

            }





        });



        return view;
    }



}
