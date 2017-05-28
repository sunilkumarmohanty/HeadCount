package fi.aalto.headcount;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import fi.aalto.headcount.Models.Faces;

public class ChartActivity extends AppCompatActivity {
    protected static final String LOG_TAG = ChartActivity.class.getSimpleName();

    LineChart chart;
    DatabaseReference facesRef;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    protected static List<Faces> memberItems = new ArrayList<>();
    private String cameraID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        chart = (LineChart) findViewById(R.id.chart);
        Intent i = getIntent();
        Bundle extras = i.getExtras();

        cameraID = extras.get("CAMERAID").toString();

        PopulateChart();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    private void PopulateChart() {
        final List<Entry> entries = new ArrayList<Entry>();

        facesRef = database.getReference(getString(R.string.FB_FACES)).child(cameraID);
        Calendar calendar1 = Calendar.getInstance(TimeZone.getTimeZone("Europe/Helsinki"));
        Log.d(LOG_TAG, "SECS "+ calendar1.getTimeInMillis()/1000);

        Calendar calendar = Calendar.getInstance();
        Log.d(LOG_TAG, "SECS "+ calendar.getTimeInMillis()/1000);

        Long toDate = calendar.getTimeInMillis()/1000;
        final Long fromDate = toDate - 3600;
        Log.d(LOG_TAG, "Dates : " + fromDate + " - " + toDate );

        Query dataRange = facesRef.orderByChild("created_at").startAt(fromDate).endAt(toDate);
        dataRange.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d(LOG_TAG,snapshot.toString());
                    Faces item = snapshot.getValue(Faces.class);
                    Long time = item.getCreated_at() - fromDate;
                    entries.add(new Entry((float)time, item.getFaces()));
                }
                if (entries.size() < 1)
                {
                    return;
                }
                LineDataSet set1 = new LineDataSet(entries, "faces"); // add entries to dataset

                IAxisValueFormatter xAxisFormatter = new XAxisFormatter(fromDate);

                XAxis xAxis = chart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//                xAxis.setTypeface(mTfLight);
                xAxis.setDrawGridLines(false);
                xAxis.setGranularity(1f); // only intervals of 1 day
                xAxis.setLabelCount(5);
                xAxis.setValueFormatter(xAxisFormatter);

                YAxis leftAxis = chart.getAxisLeft();
                leftAxis.setGranularity(1f);

                YAxis yAxisRight = chart.getAxisRight();
                yAxisRight.setEnabled(false);


                set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                set1.setColor(ColorTemplate.getHoloBlue());
                set1.setValueTextColor(ColorTemplate.getHoloBlue());
                set1.setLineWidth(1.5f);
                set1.setDrawCircles(false);
                set1.setDrawValues(false);
                set1.setFillAlpha(65);
                set1.setFillColor(ColorTemplate.getHoloBlue());
                set1.setHighLightColor(Color.rgb(244, 117, 117));
                set1.setDrawCircleHole(false);
                Log.d(LOG_TAG, "ENTRIES : " + entries.size() );
                LineData lineData = new LineData(set1);
                chart.getLegend().setEnabled(false);
                Description description = new Description();
                description.setText("");
                chart.setDescription(description);
                chart.setData(lineData);
                chart.invalidate();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                try {
                    FirebaseAuth.getInstance().signOut();
                }
                catch(Exception ex){}
                Intent loginIntent = new Intent(ChartActivity.this, LoginActivity.class);
                loginIntent.setFlags( loginIntent.getFlags());
                startActivity( loginIntent);
                finish();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
