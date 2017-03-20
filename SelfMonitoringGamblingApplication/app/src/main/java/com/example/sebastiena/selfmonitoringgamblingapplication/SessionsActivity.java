package com.example.sebastiena.selfmonitoringgamblingapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.database.FirebaseDatabase;

import FireBase.DatabaseHelper;

public class SessionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessions);
        ListView listView = (ListView) findViewById(R.id.sessionsList);
        DatabaseHelper dbHelper = new DatabaseHelper(FirebaseDatabase.getInstance().getReference());
        dbHelper.fetchDataAndDisplayList(SessionsActivity.this,listView);

    }
}