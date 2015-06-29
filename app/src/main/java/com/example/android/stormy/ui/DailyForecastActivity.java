package com.example.android.stormy.ui;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.stormy.R;
import com.example.android.stormy.adapters.DayAdapter;
import com.example.android.stormy.weather.Day;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DailyForecastActivity extends ListActivity {

    private Day[] mDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.DAILY_FORECAST);
        mDays = Arrays.copyOf(parcelables, parcelables.length, Day[].class);
//        Default Adapter.
//
//        String[] daysOfTheWeek = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                this,
//                android.R.layout.simple_list_item_1,
//                daysOfTheWeek
//        );
//        setListAdapter(adapter)
        //when a new object is created it needs to know about the activity.
        //a context is current state of application.
        //context lets newly created objects what is going on.
        //typically it gives info regarding the other applications or resources.
        DayAdapter adapter = new DayAdapter(this, mDays);
        setListAdapter(adapter);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String dayOfTheWeek = mDays[position].getDayOfTheWeek();
        String conditions = mDays[position].getSummary();
        String highTemp = mDays[position].getTemperatureMax() + "";

        String message = String.format("On %s the high will be %s and it will be %s",
                dayOfTheWeek,
                conditions,
                highTemp);

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

    }

}

//Implementing the List view in a regular activity.

//public class DailyForecastActivity extends Activity {
//
//    private Day[] mDays;
//    @InjectView(android.R.id.list) ListView mListView;
//    @InjectView(android.R.id.empty) TextView mEmptyTextView;
//
//  @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_daily_forecast);
//        ButterKnife.inject(this);
//
//        Intent intent = getIntent();
//        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.DAILY_FORECAST);
//        mDays = Arrays.copyOf(parcelables, parcelables.length, Day[].class);
//
//        DayAdapter adapter = new DayAdapter(this, mDays);
//        mListView.setAdapter(adapter);
//
//        mListView.setEmptyView(mEmptyTextView);
//
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                String dayOfTheWeek = mDays[position].getDayOfTheWeek();
//                String conditions = mDays[position].getSummary();
//                String highTemp = mDays[position].getTemperatureMax() + "";
//
//                String message = String.format("On %s the high will be %s and it will be %s",
//                        dayOfTheWeek,
//                        conditions,
//                        highTemp);
//
//                Toast.makeText(DailyForecastActivity.this, message, Toast.LENGTH_LONG).show();
//
//            }
//        });
//
//    }
//}
