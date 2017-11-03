package FireBase;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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

import com.example.sebastiena.selfmonitoringgamblingapplication.GamblingSessionFragment;
import com.example.sebastiena.selfmonitoringgamblingapplication.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Objects.GamblingSessionEntity;
import Objects.UserEntity;

/**
 * Created by SebastienA on 2017-03-16.
 */


 class MyValueFormatter implements IAxisValueFormatter {


    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return String.valueOf((int)value);
    }
}

 class CustomMarkerView extends MarkerView {

     private TextView tvContent;
     private ArrayList<GamblingSessionEntity> gsEntities;

    public CustomMarkerView (Context context, int layoutResource, ArrayList<GamblingSessionEntity> gsEntities ) {
        super(context, layoutResource);
        // this markerview only displays a textview
        tvContent = (TextView) findViewById(R.id.tvContent);
        this.gsEntities = gsEntities;

    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(final Entry e, Highlight highlight) {

                int position = (int) e.getX();
                GamblingSessionEntity gs = gsEntities.get(position);
                String date = gs.getDate();
                String game = gs.getGame();
                String mode = gs.getMode();
                String duration;
                if(gs.getDuration()>60){
                    int hours = gs.getDuration()/60;
                    int minutes = gs.getDuration()%60;
                    duration= Integer.toString(hours) +"h " + Integer.toString(minutes) + "m";
                }else{
                     duration = Integer.toString(gs.getDuration()) + "m";
                }

                
                String infoText = "Outcome: "+ e.getY()+ "\n" + "Date: " +date + "\n" + "Game: " +game +  "\n" + "Mode: " +mode +"\n" + "Duration: " +duration;

                tvContent.setSingleLine(false);
                tvContent.setMaxLines(20);
                tvContent.setText(infoText.replace("\\n", "\n")); // set the entry-value as the display text



    }


}
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

                        if (spentAmount > 50){
                            builder1.setMessage("This is your fifth session. A normal person gambles around : " + spentAmount *0.75 + ". You gambled : " + spentAmount);

                        }else{
                            builder1.setMessage("This is your fifth session. You've gambled : " + spentAmount  );

                        }
                    }else {
                        if (didBetter){
                            builder1.setMessage("Congrats you've decreased your gambling in these last 5 sessions. You went from : " + previousSpentAmount + " to : " + spentAmount);

                        }else{

                            builder1.setMessage("You went over your previous 5 gambling sessions amount spent. You went from : " + previousSpentAmount + " to : " + spentAmount);


                        }
                    }
                    builder1.setCancelable(true);

                    builder1.setNeutralButton(
                            "Ok",
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

                DatabaseReference entityRef = gsRef.child(entity.getUid());
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

    public void fetchDataAndDisplayHomePage(final Activity act, final TextView outcome,final TextView maxwon,final TextView maxlost){
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
                int maxWon = 0;
                int maxLost = 0;
                for(GamblingSessionEntity entity : gsEntities){
                    int moneyOutcome = Integer.parseInt(entity.getFinalAmount())-Integer.parseInt(entity.getStartingAmount());
                    if (maxWon<moneyOutcome){
                        maxWon = moneyOutcome;
                    }
                    if (maxLost>moneyOutcome){
                        maxLost = moneyOutcome;
                    }
                    totalOutcome = totalOutcome + moneyOutcome;
                }
                maxwon.setText("Max amount won: " +Integer.toString(maxWon) +"$");
                maxlost.setText("Max amount lost: " +Integer.toString(maxLost) +"$");
                outcome.setText( Integer.toString(totalOutcome) +"$");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
    public String getDate() {
        Date date = new Date();
        Date newDate = new Date(date.getTime() + (604800000L * 2) + (24 * 60 * 60));
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        return dt.format(newDate);

    }

    public void fetchDataAndDisplayDaily(final Activity act, final TextView spentView,final TextView madeView,final TextView lostView,final TextView outcome){
        gsEntities.clear();
        final ArrayList<GamblingSessionEntity> gsEntities = new ArrayList<>();

        Query query = db.child("gamblingSession").orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    gsEntities.add(ds.getValue(GamblingSessionEntity.class));
                }
                String date = getDate();

                int totalOutcome = 0;
                int spent = 0;
                int won = 0;
                int lost = 0;
                for(GamblingSessionEntity entity : gsEntities){
                    if (entity.getDate().equals(date)){
                        spent = Integer.parseInt(entity.getStartingAmount()) + spent;
                        int moneyOutcome = Integer.parseInt(entity.getFinalAmount())-Integer.parseInt(entity.getStartingAmount());
                        if (moneyOutcome >= 0){
                            won = moneyOutcome + won;
                        }else{

                            lost = Math.abs(moneyOutcome) + lost;
                        }
                        totalOutcome = totalOutcome + moneyOutcome;
                    }

                }
                spentView.setText("Spent: "+Integer.toString(spent) +"$");
                madeView.setText("Made: " +Integer.toString(won) +"$");
                lostView.setText("Lost: " +Integer.toString(lost) +"$");
                outcome.setText("Outcome: "+ Integer.toString(totalOutcome) +"$");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }




    public void fetchDataAndDisplayGraphs (final Activity act, final PieChart piechart, final LineChart lineChart){
        gsEntities.clear();
        final ArrayList<GamblingSessionEntity> gsEntities = new ArrayList<>();


        Query query = db.child("gamblingSession").orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    gsEntities.add(ds.getValue(GamblingSessionEntity.class));
                }
                int gain = 0;
                int loss = 0;
                List<Entry> entriesLine = new ArrayList<Entry>();
                for(GamblingSessionEntity entity : gsEntities){
                    int moneyOutcome = Integer.parseInt(entity.getFinalAmount())-Integer.parseInt(entity.getStartingAmount());
                    if (moneyOutcome > 0){
                        gain = moneyOutcome + gain;
                    } else{
                        loss = Math.abs(moneyOutcome) + loss;
                    }

                    int position = gsEntities.indexOf(entity);
                    Entry newEntry = new Entry(position,moneyOutcome);

                    entriesLine.add(newEntry);
                }

                LineDataSet lineDataSet = new LineDataSet(entriesLine,"Sessions");
                LineData lineData = new LineData(lineDataSet);
                lineChart.setData(lineData);
                lineChart.setTouchEnabled(true);
                lineChart.setHighlightPerTapEnabled(true);
                CustomMarkerView mv = new CustomMarkerView(act, R.layout.marker_layout, gsEntities);
                lineChart.setMarker(mv);
                Description description1 = new Description();
                description1.setText("Gambling Sessions");
                lineChart.setDescription(description1);
                lineChart.getXAxis().setLabelCount(gsEntities.size()+2, true);
                lineChart.getXAxis().setAxisMinimum(0f);
                lineChart.getXAxis().setAxisMaximum(gsEntities.size()+1);
                lineChart.getXAxis().setValueFormatter(new MyValueFormatter());
                lineChart.setPinchZoom(false);
                lineChart.invalidate();






                BarGraphSeries<DataPoint> bar = new BarGraphSeries<>(new DataPoint[] {
                        new DataPoint(1, gain),
                        new DataPoint(2, loss)
                });
                Description description = new Description();
                description.setText("Performance from gambling sessions played");
                piechart.setDescription(description);
                // enable hole and configure
                piechart.setDrawHoleEnabled(true);
                piechart.setHoleRadius(7);
                piechart.setTransparentCircleRadius(10);

                // enable rotation of the chart by touch
                piechart.setRotationAngle(0);
                piechart.setRotationEnabled(true);

                ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
                if (gain == 0){
                    entries.add(new PieEntry(gain,""));
                }else{
                    entries.add(new PieEntry(gain,"Gain"));
                }
                if (loss == 0){
                    entries.add(new PieEntry(loss,""));
                }else{
                    entries.add(new PieEntry(loss,"Loss"));
                }

                PieDataSet dataSet = new PieDataSet(entries, "Outcome");
                dataSet.setColors(new int[] {Color.GREEN,Color.RED });
                PieData data = new PieData();
                data.setDataSet(dataSet);
                piechart.setData(data);
                piechart.invalidate();


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


