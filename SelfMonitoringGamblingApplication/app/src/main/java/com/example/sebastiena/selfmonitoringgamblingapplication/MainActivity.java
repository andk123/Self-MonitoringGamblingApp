package com.example.sebastiena.selfmonitoringgamblingapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private Button gsButton;
    private Button sessionsButton;
    private Button graphsButton;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gsButton = (Button) findViewById(R.id.start_gs);
        sessionsButton = (Button) findViewById(R.id.sessions);
        graphsButton = (Button) findViewById(R.id.graphs);
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


    }
}
