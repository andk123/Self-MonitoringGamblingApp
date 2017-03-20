package FireBase;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sebastiena.selfmonitoringgamblingapplication.GamblingSessionActivity;
import com.example.sebastiena.selfmonitoringgamblingapplication.GraphsActivity;
import com.example.sebastiena.selfmonitoringgamblingapplication.MainActivity;
import com.example.sebastiena.selfmonitoringgamblingapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

import Objects.GamblingSessionEntity;

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






    public void fetchDataAndDisplayGraph (final Activity act, final GraphView graph){
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
                for(GamblingSessionEntity entity : gsEntities){
                    int moneyOutcome = Integer.parseInt(entity.getFinalAmount())-Integer.parseInt(entity.getStartingAmount());
                    int position = gsEntities.indexOf(entity);
                    DataPoint newPoint = new DataPoint(position,moneyOutcome);
                    series.appendData(newPoint,true,gsEntities.size());
                }
                graph.addSeries(series);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    public void fetchDataAndDisplayList(final Activity act, final ListView listView){
        gsEntities.clear();
        final ArrayList<GamblingSessionEntity> gsEntities = new ArrayList<>();

        Query query = db.child("gamblingSession").orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    gsEntities.add(ds.getValue(GamblingSessionEntity.class));
                }
                GamblingSessionAdapter adapter = new GamblingSessionAdapter(act,gsEntities);
                listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

class GamblingSessionAdapter extends ArrayAdapter<GamblingSessionEntity> {
    Activity activity;
    public GamblingSessionAdapter(Activity act, ArrayList<GamblingSessionEntity> entities) {
        super(act, 0, entities);
        activity = act;
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
        TextView id = (TextView) convertView.findViewById(R.id.gsId);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView gameType = (TextView) convertView.findViewById(R.id.gameType);
        TextView duration = (TextView) convertView.findViewById(R.id.duration);
        TextView outcome = (TextView) convertView.findViewById(R.id.outcome);

        // Populate the data into the template view using the data object
        id.setText(Integer.toString(position));
        date.setText(entity.getDate());
        gameType.setText(entity.getGame());
        duration.setText(Integer.toString(entity.getDuration()));
        int moneyOutcome = Integer.parseInt(entity.getFinalAmount())-Integer.parseInt(entity.getStartingAmount());
        outcome.setText(Integer.toString(moneyOutcome));

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goTo = new Intent(activity,GamblingSessionActivity.class);
                goTo.putExtra("GamblingSession",entity);
                activity.startActivity(goTo);

            }
        });




        // Return the completed view to render on screen
        return convertView;
    }
}
