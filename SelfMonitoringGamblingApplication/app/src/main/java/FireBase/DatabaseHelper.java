package FireBase;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.example.sebastiena.selfmonitoringgamblingapplication.GamblingSessionActivity;
import com.example.sebastiena.selfmonitoringgamblingapplication.GamblingSessionFragment;
import com.example.sebastiena.selfmonitoringgamblingapplication.GraphsActivity;
import com.example.sebastiena.selfmonitoringgamblingapplication.MainActivity;
import com.example.sebastiena.selfmonitoringgamblingapplication.R;
import com.example.sebastiena.selfmonitoringgamblingapplication.newMainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

import Objects.GamblingSessionEntity;
import Objects.UserEntity;

/**
 * Created by SebastienA on 2017-03-16.
 */

public class DatabaseHelper {

    DatabaseReference db;
    Boolean saved;
    ArrayList<GamblingSessionEntity> gsEntities = new ArrayList<>();


    public DatabaseHelper(DatabaseReference db){
        this.db = db;
    }

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }
    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }


    public void displayDialog(final Activity act){
        final ArrayList<GamblingSessionEntity> gsEntities = new ArrayList<>();

        Query query = db.child("gamblingSession").orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    gsEntities.add(ds.getValue(GamblingSessionEntity.class));
                }
                if (gsEntities.size()%5 == 0){

                    boolean didBetter = false;
                    boolean firstTime = true;
                    int spentAmount = 0;
                    for (int i = gsEntities.size()-1; i> gsEntities.size()-6;i--){
                        spentAmount = spentAmount + Integer.valueOf(gsEntities.get(i).getStartingAmount());
                    }

                    int previousSpentAmount = 0;
                    if (gsEntities.size()>=10){
                        firstTime = false;
                        for (int i = gsEntities.size()-6; i>= gsEntities.size()-10;i--){
                            previousSpentAmount = previousSpentAmount + Integer.valueOf(gsEntities.get(i).getStartingAmount());
                        }

                    }

                    if (spentAmount < previousSpentAmount){
                        didBetter = true;
                    }

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(act);

                    if (firstTime){
                        builder1.setMessage("This is your fifth session. A normal person gambles around : " + spentAmount *0.75 + ". You gambled : " + spentAmount);
                    }else {
                        if (didBetter){
                            builder1.setMessage("Congrats you've decreased your gambling in these last 5 sessions. You went from : " + previousSpentAmount + " to : " + spentAmount);

                        }else{

                            builder1.setMessage("You went over your previous 5 gambling sessions amount spent. You went from : " + previousSpentAmount + " to : " + spentAmount);


                        }
                    }
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }




    public boolean saveGamblingSession(GamblingSessionEntity entity){
        if (entity == null){
            saved = false;
        }else{
            try{
                DatabaseReference gsRef = db.child("gamblingSession");
                DatabaseReference newGsRef = gsRef.push();
                String key = newGsRef.getKey();
                entity.setKey(key);
                newGsRef.setValue(entity);
                saved = true;

            }catch(DatabaseException e){
                e.printStackTrace();
                saved = false;

            }
        }

        return saved;
    }


    public boolean updateUserEntity(UserEntity entity){
        if (entity == null){
            saved = false;
        }else{
            try{
                DatabaseReference gsRef = db.child("users");

                DatabaseReference entityRef = gsRef.child(EncodeString(entity.getEmail()));
                entityRef.setValue(entity);
                saved = true;
            }catch(DatabaseException e){
                e.printStackTrace();
                saved = false;

            }
        }

        return saved;
    }



    public boolean updateGamblingSession(GamblingSessionEntity entity){
        if (entity == null){
            saved = false;
        }else{
            try{
                DatabaseReference gsRef = db.child("gamblingSession");
                DatabaseReference entityRef = gsRef.child(entity.getKey());
                entityRef.setValue(entity);
                saved = true;

            }catch(DatabaseException e){
                e.printStackTrace();
                saved = false;

            }
        }

        return saved;
    }

    public void fetchDataAndDisplayOutcome(final Activity act, final TextView outcome){
        gsEntities.clear();
        final ArrayList<GamblingSessionEntity> gsEntities = new ArrayList<>();

        Query query = db.child("gamblingSession").orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    gsEntities.add(ds.getValue(GamblingSessionEntity.class));
                }
               int totalOutcome = 0;
                for(GamblingSessionEntity entity : gsEntities){
                    int moneyOutcome = Integer.parseInt(entity.getFinalAmount())-Integer.parseInt(entity.getStartingAmount());
                    totalOutcome = totalOutcome + moneyOutcome;
                }
                outcome.setText("Your current balance is  " + Integer.toString(totalOutcome) +"$");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }



    public void fetchDataAndDisplayGraphs (final Activity act, final GraphView graph1, final GraphView graph2){
        gsEntities.clear();
        final ArrayList<GamblingSessionEntity> gsEntities = new ArrayList<>();

        Query query = db.child("gamblingSession").orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    gsEntities.add(ds.getValue(GamblingSessionEntity.class));
                }
                LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
                int gain = 0;
                int loss = 0;
                for(GamblingSessionEntity entity : gsEntities){
                    int moneyOutcome = Integer.parseInt(entity.getFinalAmount())-Integer.parseInt(entity.getStartingAmount());
                    if (moneyOutcome > 0){
                        gain = moneyOutcome + gain;
                    } else{
                        loss = Math.abs(moneyOutcome) + loss;
                    }
                    int position = gsEntities.indexOf(entity);
                    DataPoint newPoint = new DataPoint(position,moneyOutcome);
                    series.appendData(newPoint,true,gsEntities.size());
                }
                BarGraphSeries<DataPoint> bar = new BarGraphSeries<>(new DataPoint[] {
                        new DataPoint(1, gain),
                        new DataPoint(2, loss)
                });
               ;
                bar.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                    @Override
                    public int get(DataPoint data) {
                        if(data.getX()==1){
                            return Color.rgb(0,255,0);
                        }else{
                            return Color.rgb(255,0,0);
                        }
                    }
                });
                graph2.addSeries(bar);
                graph2.setTitle("Gain vs Loss");
                graph1.addSeries(series);
                graph1.setTitle("Gambling Outcomes");
                StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph2);
                String[] stringList = {"Gain", "Loss"};
                staticLabelsFormatter.setHorizontalLabels(stringList);
                graph2.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
                graph2.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
                GridLabelRenderer gridLabel = graph1.getGridLabelRenderer();
                gridLabel.setHorizontalAxisTitle("Sessions");
                gridLabel.setVerticalAxisTitle("Final Amount");


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    public void fetchDataAndDisplayList(final Context c, final ListView listView){
        gsEntities.clear();
        final ArrayList<GamblingSessionEntity> gsEntities = new ArrayList<>();

        Query query = db.child("gamblingSession").orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    gsEntities.add(ds.getValue(GamblingSessionEntity.class));
                }
                GamblingSessionAdapter adapter = new GamblingSessionAdapter(c,gsEntities);
                listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

class GamblingSessionAdapter extends ArrayAdapter<GamblingSessionEntity> {
    Context c;

    public GamblingSessionAdapter(Context c, ArrayList<GamblingSessionEntity> entities) {
        super(c, 0, entities);
        this.c = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final GamblingSessionEntity entity = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_gambling_session, parent, false);
        }
        // Lookup view for data population
        Button editButton = (Button) convertView.findViewById(R.id.edit);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView gameType = (TextView) convertView.findViewById(R.id.gameType);
        TextView duration = (TextView) convertView.findViewById(R.id.duration);
        TextView outcome = (TextView) convertView.findViewById(R.id.outcome);

        // Populate the data into the template view using the data object
        date.setText(entity.getDate());
        gameType.setText(entity.getGame());
        duration.setText(Integer.toString(entity.getDuration()));
        int moneyOutcome = Integer.parseInt(entity.getFinalAmount()) - Integer.parseInt(entity.getStartingAmount());
        outcome.setText(Integer.toString(moneyOutcome));

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity act = (Activity) c;
                ((Activity) c).getIntent().putExtra("GamblingSession", entity);
                Fragment fragment = (Fragment) GamblingSessionFragment.newInstance();
                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = ((FragmentActivity) c).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

            }
        });


        // Return the completed view to render on screen
        return convertView;
    }
}


