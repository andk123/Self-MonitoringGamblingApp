package com.example.sebastiena.selfmonitoringgamblingapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.FirebaseDatabase;
import com.jjoe64.graphview.GraphView;

import FireBase.DatabaseHelper;

public class GraphsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);
        GraphView graph = (GraphView) findViewById(R.id.graph1);
        DatabaseHelper dbHelper = new DatabaseHelper(FirebaseDatabase.getInstance().getReference());
        dbHelper.fetchDataAndDisplayGraph(GraphsActivity.this,graph);
    }
}
