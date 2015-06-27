package com.example.android.stormy.ui;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import com.example.android.stormy.R;
import com.example.android.stormy.adapters.DayAdapter;
import com.example.android.stormy.weather.Day;

public class DailyForecastActivity extends ListActivity {

    private Day[] mDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);

//        Default Adapter.
//
//        String[] daysOfTheWeek = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                this,
//                android.R.layout.simple_list_item_1,
//                daysOfTheWeek
//        );
//        setListAdapter(adapter);
        //context gives the resources and classes present with the activity.
        //when a new object is created it needs to know about the activity.
        //a context is current state of application.
        DayAdapter adapter = new DayAdapter(this, mDays);

    }

}
