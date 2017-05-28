package fi.aalto.headcount.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import fi.aalto.headcount.ChartActivity;
import fi.aalto.headcount.ManageCameraActivity;
import fi.aalto.headcount.Models.Camera;
import fi.aalto.headcount.R;

/**
 * Created by sunil on 24-05-2017.
 */

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.CameraItemHolder> {
    private Context context;
    private List<Camera> memberItems;

    DatabaseReference cameraItemRef;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final String LOG_TAG = DashboardAdapter.class.getSimpleName();

    static class CameraItemHolder extends RecyclerView.ViewHolder {
        ImageView mcv_image;
        TextView camera_name, camera_IP, item_count;
        CardView cv;
        public CameraItemHolder(View itemView) {
            super(itemView);
            itemView.getContext();
            camera_name = (TextView) itemView.findViewById(R.id.camera_name);
            camera_IP = (TextView) itemView.findViewById(R.id.camera_IP);
            item_count = (TextView) itemView.findViewById(R.id.item_count);
            cv = (CardView) itemView.findViewById(R.id.cv);
        }
    }
    public DashboardAdapter(Context context,
                            List<Camera> memberItems) {
        this.context = context;
        this.memberItems = memberItems;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public CameraItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.camera_item_cv_item, viewGroup, false);
        CameraItemHolder pvh = new CameraItemHolder(v);
        return pvh;
    }
    @Override
    public void onBindViewHolder(final CameraItemHolder viewHolder, final int i) {
        final Camera memberItem = memberItems.get(i);
        viewHolder.camera_name.setText(memberItem.getName());
        viewHolder.camera_IP.setText(memberItem.getIP());
        viewHolder.item_count.setText(memberItem.getFaces().toString());

        viewHolder.cv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {



                PopupMenu popup = new PopupMenu(context, view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.camera_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new MyMenuItemClickListener(memberItem));
                popup.show();



                return true;
            }
        });


        viewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Camera selectedItem = (Camera) memberItem;
                Intent chartIntent = new  Intent(context, ChartActivity.class);
                chartIntent.putExtra("CAMERAID", selectedItem.getId());
                context.startActivity(chartIntent);
            }});
    }

    private void DeleteCamera(Camera memberItem) {
        DatabaseReference listRef = database.getReference("cameras").child(memberItem.getId());
        listRef.setValue(null);
    }

    @Override
    public int getItemCount() {
        return memberItems.size();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        Camera cam ;
        public MyMenuItemClickListener(Camera cam) {
            this.cam = cam;
        }
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_edit:
                    Intent addCameraIntent = new Intent(context, ManageCameraActivity.class);
                    addCameraIntent.setFlags( addCameraIntent.getFlags());
                    addCameraIntent.putExtra("CAMERAID", cam.getId());
                    context.startActivity( addCameraIntent);
                    return true;
                case R.id.action_delete:
                    DeleteCamera(cam);
                    return true;
            }
            return false;
        }
    }

}
