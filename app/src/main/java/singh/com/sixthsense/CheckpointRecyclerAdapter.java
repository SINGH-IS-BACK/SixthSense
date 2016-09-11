package singh.com.sixthsense;

/**
 * Created by kthethi on 03/09/15.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import singh.com.sixthsense.model.Checkpoint;

//search user & message_directory
public class CheckpointRecyclerAdapter extends RecyclerView.Adapter<CheckpointRecyclerAdapter.MyViewHolder> {

    private Context mContext;
    public ArrayList<Checkpoint> mCheckpoint;
    private static final int TYPE_NOT_SELECTED = 0;
    private static final int TYPE_SELECTED = 1;
    int mCurrentLcoation  = 0;


    public CheckpointRecyclerAdapter(Context context, ArrayList<Checkpoint> users, int currentLocation){
        mContext = context;
        mCheckpoint = users;
        mCurrentLcoation = currentLocation;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SELECTED) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.checkpoint_item_selected, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(itemView);
            return viewHolder;
        }
        else{
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.checkpoint_item, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(itemView);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        Checkpoint checkpoint = (Checkpoint) this.getItem(position);
        viewHolder.location.setText(checkpoint.getSource().getLocation());
        viewHolder.message.setText(checkpoint.getVoiceText());
    }

    @Override
    public int getItemCount() {
        return mCheckpoint.size();
    }

    public Object getItem(int position) {
        return mCheckpoint.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        Checkpoint checkpoint = (Checkpoint)this.getItem(position);
        if (checkpoint != null && checkpoint.getSource().getMinor() == mCurrentLcoation) {
            return TYPE_SELECTED;
        }
        return TYPE_NOT_SELECTED;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public final TextView location;
        public final TextView message;

        public MyViewHolder (View view){
            super(view);

            location = (TextView) view.findViewById(R.id.location);
            message = (TextView) view.findViewById(R.id.message);
        }
    }


}