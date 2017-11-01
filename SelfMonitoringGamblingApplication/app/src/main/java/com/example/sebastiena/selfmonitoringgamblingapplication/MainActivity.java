package com.example.sebastiena.selfmonitoringgamblingapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import FireBase.DatabaseHelper;

public class MainActivity extends AppCompatActivity {
    private Button gsButton;
    private Button sessionsButton;
    private Button graphsButton;
    private TextView outcome;
    private TextView date;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gsButton = (Button) findViewById(R.id.start_gs);
        sessionsButton = (Button) findViewById(R.id.sessions);
        graphsButton = (Button) findViewById(R.id.graphs);
        outcome = (TextView) findViewById(R.id.outcome);
        date = (TextView) findViewById(R.id.date);
                //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        gsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GamblingSessionActivity.class));
            }
        });

        sessionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SessionsActivity.class));
            }
        });

        graphsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GraphsActivity.class));
            }
        });

        date.setText("Current date: " + getDate());
        DatabaseHelper dbHelper = new DatabaseHelper(FirebaseDatabase.getInstance().getReference());




    }

    public String getDate() {
        Date date = new Date();
        Date newDate = new Date(date.getTime() + (604800000L * 2) + (24 * 60 * 60));
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        return dt.format(newDate);

    }
}
