package FireBase;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.example.sebastiena.selfmonitoringgamblingapplication.GamblingSessionFragment;
import com.example.sebastiena.selfmonitoringgamblingapplication.R;
import com.example.sebastiena.selfmonitoringgamblingapplication.calendar.bean.CalendarDate;
import com.example.sebastiena.selfmonitoringgamblingapplication.calendar.utils.StringUtils;
import com.example.sebastiena.selfmonitoringgamblingapplication.calendar.view.CommonCalendarView;
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
import com.github.mikephil.charting.utils.ColorTemplate;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                if (gsEntities.size()%2 == 0){

                    boolean didBetter = false;
                    boolean firstTime = true;
                    int spentAmount = 0;
                    for (int i = gsEntities.size()-1; i> gsEntities.size()-3;i--){
                        spentAmount = spentAmount + Integer.valueOf(gsEntities.get(i).getStartingAmount());
                    }

                    int previousSpentAmount = 0;
                    if (gsEntities.size()>=4){
                        firstTime = false;
                        for (int i = gsEntities.size()-3; i>= gsEntities.size()-4;i--){
                            previousSpentAmount = previousSpentAmount + Integer.valueOf(gsEntities.get(i).getStartingAmount());
                        }

                    }

                    if (spentAmount < previousSpentAmount){
                        didBetter = true;
                    }

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(act);

                    if (firstTime){

                        if (spentAmount > 50){
                            builder1.setMessage("This is your second session. A normal person gambles around : " + spentAmount *0.75 + ". You gambled : " + spentAmount);

                        }else{
                            builder1.setMessage("This is your second session. You've gambled : " + spentAmount  );

                        }
                    }else {
                        if (didBetter){
                            builder1.setMessage("Congrats you've decreased your gambling in these last 2 sessions. You went from : " + previousSpentAmount + " to : " + spentAmount);

                        }else{

                            builder1.setMessage("You went over your previous 2 gambling sessions amount spent. You went from : " + previousSpentAmount + " to : " + spentAmount);


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


    public void getDailyLimit(final Activity act) {

        Query query = db.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserEntity currentUser = dataSnapshot.getValue(UserEntity.class);
                int budgetLimit = Integer.parseInt(currentUser.getDailyLimit());
                int timeLimit = Integer.parseInt(currentUser.getDailyTimeLimit());
                displayLimitDialog(act, budgetLimit,timeLimit);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });


    }






    public void displayLimitDialog(final Activity act, final int budgetLimit, final int timeLimit) {
        final ArrayList<GamblingSessionEntity> gsEntities = new ArrayList<>();

        Query query = db.child("gamblingSession").orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String date = getDate();
                int spent = 0;
                int time = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    gsEntities.add(ds.getValue(GamblingSessionEntity.class));
                }
                for (GamblingSessionEntity entity : gsEntities) {
                    if (entity.getDate().equals(date)) {
                        time = entity.getDuration() + time;
                        spent = Integer.parseInt(entity.getStartingAmount()) + spent;
                    }

                }
                if (budgetLimit != 0 && timeLimit != 0) {

                    if (spent >= budgetLimit & time >= timeLimit) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(act);
                        builder1.setMessage("WARNING: You've exceeded your daily budget and time limit. Time Limit is set to: " + timeLimit + " You are at: " + time + ". Budget Limit is set to:"+ budgetLimit + " You are at: " + spent);

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

                    } else if (spent >= budgetLimit) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(act);
                        builder1.setMessage("WARNING: You've exceeded your daily budget limit. Limit is set to: " + budgetLimit + " You are at: " + spent);

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

                    } else if (time >= timeLimit) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(act);
                        builder1.setMessage("WARNING: You've exceeded your daily time limit. Limit is set to: " + timeLimit + " You are at: " + time);

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


    public boolean updateUserDailyLimit( String uid, int limit,int time){
    boolean saved;
            try{
                DatabaseReference gsRef = db.child("users");
                DatabaseReference entityRef = gsRef.child(uid);
                entityRef.child("dailyLimit").setValue(Integer.toString(limit));
                entityRef.child("dailyTimeLimit").setValue(Integer.toString(time));
                saved = true;
            }catch(DatabaseException e){
                e.printStackTrace();
                saved = false;

            }


        return saved;
    }



    public boolean updateUserEntity(String uid, String yob, String sex){
        boolean saved;
        try{
            DatabaseReference gsRef = db.child("users");
            DatabaseReference entityRef = gsRef.child(uid);
            entityRef.child("yob").setValue(yob);
            entityRef.child("sex").setValue(sex);
            saved = true;
        }catch(DatabaseException e){
            e.printStackTrace();
            saved = false;

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

    public void fetchDataAndDisplayHomePage(final Activity act, final TextView outcome,final TextView maxwon,final TextView maxlost,final TextView time){
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
                int totalTime = 0;
                for(GamblingSessionEntity entity : gsEntities){
                    int moneyOutcome = Integer.parseInt(entity.getFinalAmount())-Integer.parseInt(entity.getStartingAmount());
                    totalTime = entity.getDuration() + totalTime;
                    if (maxWon<moneyOutcome){
                        maxWon = moneyOutcome;
                    }
                    if (maxLost>moneyOutcome){
                        maxLost = moneyOutcome;
                    }
                    totalOutcome = totalOutcome + moneyOutcome;
                }
                String duration;
                if(totalTime>60){
                    int hours = totalTime/60;
                    int minutes = totalTime%60;
                    duration= Integer.toString(hours) +"h " + Integer.toString(minutes) + "m";
                }else{
                    duration = totalTime + "m";
                }



                String maxWonStr = maxWon < 0 ? "-$"+Math.abs(maxWon) : "$"+maxWon;
                String maxLostStr = maxLost < 0 ? "-$"+Math.abs(maxLost) : "$"+maxLost;
                String totalOutcomeStr = totalOutcome < 0 ? "-$"+Math.abs(totalOutcome) : "$"+totalOutcome;

                time.setText("Total playing time: " + duration);
                maxwon.setText("Max amount won: " + maxWonStr + "");
                maxlost.setText("Max amount lost: " + maxLostStr + "");
                outcome.setText( totalOutcomeStr);
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

    public void fetchDataAndDisplayCalendar(final List<CalendarDate> mCalendarDateList, final View view, final Activity act){
        gsEntities.clear();
        final ArrayList<GamblingSessionEntity> gsEntities = new ArrayList<>();

        //Fetch sessions from the database
        Query query = db.child("gamblingSession").orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    gsEntities.add(ds.getValue(GamblingSessionEntity.class));
                }

                CalendarDate calendarDate = new CalendarDate();
                calendarDate.setOutcome(0);
                String date = gsEntities.get(0).getDate();

                //Get the outcome of each session, sum it for each date, and add it to the list of session dates
                for(GamblingSessionEntity entity : gsEntities){
                    if (!entity.getDate().equals(date)) {
                        calendarDate.setOutcomeDate(date);
                        mCalendarDateList.add(calendarDate);

                        calendarDate = new CalendarDate();
                        calendarDate.setOutcome(0);
                        date = entity.getDate();
                    }

                    int sessionOutcome = Integer.parseInt(entity.getFinalAmount())-Integer.parseInt(entity.getStartingAmount());
                    double newOutcome = calendarDate.getOutcome() + sessionOutcome;
                    calendarDate.setOutcome(newOutcome);

                }

                calendarDate.setOutcomeDate(date);
                mCalendarDateList.add(calendarDate);


                //Display the sessions on the application
                CommonCalendarView calendarView;
                final Map<String,List> mYearMonthMap = new HashMap<>();

                for (CalendarDate calendarDate2 : mCalendarDateList) {
                    calendarDate.getOutcomeDate();
                    String yearMonth = TextUtils.substring(calendarDate.getOutcomeDate(), 0, TextUtils.lastIndexOf(calendarDate.getOutcomeDate(), '-'));
                    List list = mYearMonthMap.get(yearMonth);
                    if (list == null) {
                        list = new ArrayList();
                        list.add(calendarDate2);
                        mYearMonthMap.put(yearMonth, list);
                    } else {
                        list.add(calendarDate2);
                    }
                }

                calendarView = (CommonCalendarView) view.findViewById(R.id.calendarView);

                //Set the minimum date on Calendar
//                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//                try {
//                    Date minDate = sdf.parse("01/01/2017");
//                    calendarView.setMinDate(minDate);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
                
                calendarView.init(new CommonCalendarView.DatePickerController() {
                    @Override
                    public int getMaxYear() {
                        return 2050;
                    }

                    @Override
                    public void onDayOfMonthSelected(int year, int month, int day) {
                        Toast.makeText(act, String.format("%s-%s-%s", year, StringUtils.leftPad(String.valueOf(month),2,"0"),
                                StringUtils.leftPad(String.valueOf(day),2,"0")), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDayOfMonthAndDataSelected(int year, int month, int day, List obj) {
                        if (obj==null){
                            return;
                        }
                        String priceDate = String.format("%s-%s-%s", year,
                                StringUtils.leftPad(month + "", 2, "0"), StringUtils.leftPad(String.valueOf(day), 2, "0"));
                        for (int i = 0; i < obj.size(); i++) {
                            CalendarDate datePrice = (CalendarDate) obj.get(i);
                            if (datePrice==null){
                                continue;
                            }
                            if (TextUtils.equals(datePrice.getOutcomeDate(),priceDate)){
                                Toast.makeText(act, datePrice.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void showOtherFields(Object obj, View view, int gridItemYear, int gridItemMonth, int gridItemDay) {
                        //当你设置了数据源之后，界面渲染会循环调用showOtherFields方法，在该方法中实现同一日期设置界面显示效果。
                        CalendarDate calendarDate = (CalendarDate) obj;
                        if (TextUtils.equals(calendarDate.getOutcomeDate(), String.format("%s-%s-%s", gridItemYear,
                                StringUtils.leftPad(gridItemMonth + "", 2, "0"), StringUtils.leftPad(String.valueOf(gridItemDay), 2, "0")))) {
                            CommonCalendarView.GridViewHolder viewHolder = (CommonCalendarView.GridViewHolder) view.getTag();
                            viewHolder.mPriceTv.setText(String.format("$ %s", calendarDate.getOutcome()));
                            view.setEnabled(true);
                            viewHolder.mTextView.setEnabled(true);
                        }
                    }

                    @Override
                    public Map<String, List> getDataSource() {
                        return mYearMonthMap;
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void fetchDataAndDisplayDaily(final Activity act, final TextView spentView, final TextView madeView, final TextView lostView, final TextView outcome, final EditText budget, final EditText time){
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
                String spentStr = spent < 0 ? "-$"+Math.abs(spent) : "$"+spent;
                String wonStr = won < 0 ? "-$"+Math.abs(won) : "$"+won;
                String lostStr = spent < 0 ? "-$"+Math.abs(lost) : "$"+lost;
                String totalOutcomeStr = totalOutcome < 0 ? "-$"+Math.abs(totalOutcome) : "$"+totalOutcome;

                spentView.setText("Spent: "+ spentStr +"");
                madeView.setText("Made: " + wonStr +"");
                lostView.setText("Lost: " + lostStr +"");
                outcome.setText("Outcome: "+ totalOutcomeStr +"");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Query query2 = db.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserEntity currentUser = dataSnapshot.getValue(UserEntity.class);
                budget.setText("$"+currentUser.getDailyLimit() , TextView.BufferType.EDITABLE);
                time.setText(currentUser.getDailyTimeLimit()+"m", TextView.BufferType.EDITABLE);

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
                lineDataSet.setColor(ColorTemplate.rgb("#044848"));
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
                dataSet.setColors(new int[] {ColorTemplate.rgb("#00ff80"),ColorTemplate.rgb("#ff4d4d")});
                PieData data = new PieData();
                data.setDataSet(dataSet);
                data.setValueTextSize(10f);
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
                Collections.reverse(gsEntities);
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


