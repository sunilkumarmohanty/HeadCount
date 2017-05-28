package fi.aalto.headcount;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import fi.aalto.headcount.Adapters.DashboardAdapter;
import fi.aalto.headcount.Models.Camera;

public class Dashboard extends AppCompatActivity {
    DatabaseReference cameraListRef;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    protected static List<Camera> memberItems = new ArrayList<>();
    protected static final String LOG_TAG = Dashboard.class.getSimpleName();
    private RecyclerView rv;
    DashboardAdapter rvadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        rv = (RecyclerView) findViewById(R.id.rv_Cameras);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        PopulateMembers();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }
    private void PopulateMembers() {
        rvadapter = new DashboardAdapter(this, memberItems);
        cameraListRef =database.getReference(getString(R.string.FB_CAMERAS));

        cameraListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                memberItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Camera item = snapshot.getValue(Camera.class);
                    item.setId(snapshot.getKey());
                    memberItems.add(item);
                }
                rvadapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        rv.setAdapter(rvadapter);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                try {
                    FirebaseAuth.getInstance().signOut();
                }
                catch(Exception ex){}
                Intent loginIntent = new Intent(Dashboard.this, LoginActivity.class);
                loginIntent.setFlags( loginIntent.getFlags());
                startActivity( loginIntent);
                finish();
                return true;
            case R.id.action_addcamera:
                Intent addCameraIntent = new Intent(Dashboard.this, ManageCameraActivity.class);
                addCameraIntent.setFlags( addCameraIntent.getFlags());
                startActivity( addCameraIntent);
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
