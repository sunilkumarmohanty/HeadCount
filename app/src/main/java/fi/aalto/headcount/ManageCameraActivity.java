package fi.aalto.headcount;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import fi.aalto.headcount.Models.Camera;

public class ManageCameraActivity extends AppCompatActivity {
    protected static final String LOG_TAG = ManageCameraActivity.class.getSimpleName();

    DatabaseReference cameraRef;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String cameraID;
    private TextView tv_Name, tv_IP, tv_Error;
    Camera cam;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_camera);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        try {
            cameraID = extras.get("CAMERAID").toString();
        }catch(Exception ex) { cameraID = "";}
        tv_Name = (EditText)findViewById(R.id.camera_name);
        tv_IP = (EditText)findViewById(R.id.camera_IP);
        tv_Error = (TextView) findViewById(R.id.camera_Error);

        Populate();

        findViewById(R.id.camera_Save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !TextUtils.isEmpty(tv_Name.getText().toString()) &&  !TextUtils.isEmpty(tv_IP.getText().toString())) {
                    tv_Error.setVisibility(View.INVISIBLE);
                    cameraRef = database.getReference(getString(R.string.FB_CAMERAS));
                    if (TextUtils.isEmpty(cameraID)) {
                        cameraID = cameraRef.push().getKey();
                    } else {
                        cameraRef = database.getReference(getString(R.string.FB_CAMERAS));
                        cameraRef.child(cameraID).child("IP").setValue(tv_IP.getText().toString());
                        cameraRef.child(cameraID).child("name").setValue(tv_Name.getText().toString());

                        Intent intent = new Intent(ManageCameraActivity.this, Dashboard.class);
                        startActivity(intent);
                    }
                }
                else {
                    tv_Error.setText("Name or IP address missing");
                    tv_Error.setVisibility(View.VISIBLE);
                }
            }});
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    private void Populate() {
        Log.d(LOG_TAG, cameraID);
        if( !TextUtils.isEmpty(cameraID)) {
            cameraRef = database.getReference("cameras").child(cameraID);
            cameraRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d(LOG_TAG,"o "+dataSnapshot.getValue().toString());
                            cam = dataSnapshot.getValue(Camera.class);
                            tv_Name.setText(cam.getName());
                            tv_IP.setText( cam.getIP());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("TodoApp", "getUser:onCancelled", databaseError.toException());
                        }
                    });
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                try {
                    FirebaseAuth.getInstance().signOut();
                }
                catch(Exception ex){}
                Intent loginIntent = new Intent(ManageCameraActivity.this, LoginActivity.class);
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
