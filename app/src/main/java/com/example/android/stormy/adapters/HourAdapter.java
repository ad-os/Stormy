package com.example.android.stormy.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.stormy.R;
import com.example.android.stormy.weather.Hour;

import butterknife.InjectView;

/**
 * Created by adhyan on 28/6/15.
 */
public class HourAdapter extends RecyclerView.Adapter<HourAdapter.HourViewHolder> {

    private Hour[] mHours;
    private Context mContext;

    public HourAdapter(Hour[] hours, Context context){
        mHours = hours;
        mContext = context;
    }

    //The hourView holder is called at the time a view holder is created.
    //The below three are adapter methods.
    @Override
    public HourViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.hourly_list_item, parent, false);
        HourViewHolder hourViewHolder = new HourViewHolder(v);
        return hourViewHolder;
    }

    //This method bridge between the adapter and the bind method we defined in the view holder.
    @Override
    public void onBindViewHolder(HourViewHolder holder, int position) {

        holder.bindHour(mHours[position]);
    }

    @Override
    public int getItemCount() {
        return mHours.length;
    }

    //The below code is for implementing a view holder.
    //The view holder is responsible for both mapping the data and holding the view unlike the view holder in the listView.
    public class HourViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView mTimeLabel;
        public TextView mSummaryLabel;
        public TextView mTemperatureLabel;
        public ImageView mIconImageView;

        public HourViewHolder(View itemView) {
            super(itemView);

            mIconImageView = (ImageView) itemView.findViewById(R.id.iconImageView);
            mSummaryLabel = (TextView) itemView.findViewById(R.id.summaryLabel);
            mTemperatureLabel = (TextView) itemView.findViewById(R.id.temperatureLabel);
            mTimeLabel = (TextView) itemView.findViewById(R.id.timeLabel);

            itemView.setOnClickListener(this);
        }

        public void bindHour(Hour hour){
            mTimeLabel.setText(hour.getHour());
            mSummaryLabel.setText(hour.getSummary());
            mTemperatureLabel.setText(hour.getTemperature() + "");
            mIconImageView.setImageResource(hour.getICon());
        }

        //we can display a new activity, a dialog or a toast.

        @Override
        public void onClick(View v) {

            String time = mTimeLabel.getText().toString();
            String temperature = mTemperatureLabel.getText().toString();
            String summary = mSummaryLabel.getText().toString();
            String message = String .format("At %s it will be %s and %s",
                    time,
                    temperature,
                    summary);

            //It needs to know which activity started this adapter.

            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        }

    }
}
