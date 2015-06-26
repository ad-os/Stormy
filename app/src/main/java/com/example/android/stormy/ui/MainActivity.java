package com.example.android.stormy.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.stormy.R;
import com.example.android.stormy.weather.Current;
import com.example.android.stormy.weather.Day;
import com.example.android.stormy.weather.ForeCast;
import com.example.android.stormy.weather.Hour;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private double mCurrentLatitude;
    private double mCurrentLongitude;
    private ForeCast mForeCast;

    //@function is called as annotation as it expands the one line of code.
    @InjectView(R.id.timeLabel) TextView mTimeLabel;
    @InjectView(R.id.temperatureLabel) TextView mTemperatureLabel;
    @InjectView(R.id.humidityValue) TextView mHumidityValue;
    @InjectView(R.id.precipValue) TextView mPrecipValue;
    @InjectView(R.id.summaryLabel) TextView mSummaryLabel;
    @InjectView(R.id.iconImageView) ImageView mIconImageView;
    @InjectView(R.id.refreshImageView) ImageView mRefreshImageView;
    @InjectView(R.id.progressBar) ProgressBar mProgressBar;
    @InjectView(R.id.locationLabel) TextView mLocationLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        Intent intent = new Intent(this, MapsActivity.class);
        startActivityForResult(intent, 1);

        mProgressBar.setVisibility(View.INVISIBLE);
        Log.v(TAG, "Executing main thread");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            mCurrentLatitude = data.getDoubleExtra("latitude", 37.8267);
            mCurrentLongitude = data.getDoubleExtra("longitude", -122.423);
            getForecast(mCurrentLatitude, mCurrentLongitude);
            mRefreshImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getForecast(mCurrentLatitude, mCurrentLongitude);
                }
            });
        }
    }

    public void getForecast(double latitude, double longitude) {


        String apiKey = "da6e8c57e760581f8262058f6a28c70c";

        String forecastUrl = "https://api.forecast.io/forecast/" + apiKey +
                "/" + latitude + "," + longitude + "?units=si" + "";

        if(isNetworkConnected()) {

            toggleRefresh();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().
                    url(forecastUrl).
                    build();

            Call call = client.newCall(request);


            call.enqueue(new Callback() {

                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    alertUserAboutError();
                    Log.e(TAG, "Exception caught : ", e);
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mForeCast = parseForecastDetails(jsonData);
                            //now we want to display these details on the main thread.
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught : ", e);
                    } catch (JSONException e){
                        Log.e(TAG, "Exception caught : ", e);
                    }
                }
            });
        }else {
            Toast.makeText(this, "No network available", Toast.LENGTH_LONG).show();
        }
    }

    public void toggleRefresh() {
        if(mProgressBar.getVisibility() == View.INVISIBLE) {
        mProgressBar.setVisibility(View.VISIBLE);
        mRefreshImageView.setVisibility(View.INVISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }
    }

    private void updateDisplay() {

        Current current = mForeCast.getCurrent();

        mTemperatureLabel.setText(current.getTemperature() + "");
        mTimeLabel.setText("At " + current.getFormattedTime() + " it will be");
        mHumidityValue.setText(current.getHumidity() + "");
        mPrecipValue.setText(current.getPrecipChance() + "%");
        mSummaryLabel.setText(current.getSummary());
        mLocationLabel.setText(current.getLocationLabel());

        Drawable drawable = getResources().getDrawable(current.getIconId());
        mIconImageView.setImageDrawable(drawable);
    }


    private ForeCast parseForecastDetails(String jsonData) throws JSONException{

        ForeCast foreCast = new ForeCast();

        foreCast.setCurrent(getCurrentDetails(jsonData));
        foreCast.setHourlyForecast(getHourlyForecast(jsonData));
        foreCast.setDailyForecast(getDailyForecast(jsonData));

        return foreCast;
    }

    private Day[] getDailyForecast(String jsonData) throws JSONException{
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");

        Day[] days = new Day[data.length()];

        for (int i = 0; i < data.length(); i++){

            JSONObject jsonDaily = data.getJSONObject(i);
            Day day = new Day();

            day.setSummary(jsonDaily.getString("summary"));
            day.setTemperatureMax(jsonDaily.getDouble("temperatureMax"));
            day.setIcon(jsonDaily.getString("icon"));
            day.setTime(jsonDaily.getLong("time"));
            day.setTimeZone(timezone);

            days[i] = day;
        }
        return days;
    }

    private Hour[] getHourlyForecast(String jsonData) throws JSONException{
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONArray data = hourly.getJSONArray("data");

        Hour[] hours = new Hour[data.length()];

        for (int i = 0; i < data.length(); i++){

            JSONObject jsonHour = data.getJSONObject(i);
            Hour hour = new Hour();

            hour.setSummary(jsonHour.getString("summary"));
            hour.setTemperature(jsonHour.getDouble("temperature"));
            hour.setICon(jsonHour.getString("icon"));
            hour.setTime(jsonHour.getLong("time"));
            hour.setTimeZone(timezone);

            hours[i] = hour;

        }
        return hours;
    }

    //here with throws key word we are passing the responsibility to whoever calls this method.
    private Current getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject currently = forecast.getJSONObject("currently");

        Current current = new Current();
        current.setIcon(currently.getString("icon"));
        current.setHumidity(currently.getDouble("humidity"));
        current.setPrecipChance(currently.getInt("precipProbability"));
        current.setSummary(currently.getString("summary"));
        current.setTemperature(currently.getDouble("temperature"));
        current.setTime(currently.getLong("time"));
        current.setTimeZone(timezone);
        current.setLocationLabel(timezone);
        Log.i(TAG, "From Json" + timezone);

        return current;
    }

    private boolean isNetworkConnected() {
        //Context contains the information about the application resources and classes.
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = false;
        //networkInfo != null to check if network is available
        //networkInfo.isConnected() to check if network is available.
        if (networkInfo != null && networkInfo.isConnected()){
            isConnected = true;
        }
        return isConnected;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialogFragment = new AlertDialogFragment();
        dialogFragment.show(getFragmentManager(), "error_dialog");
    }

    @OnClick(R.id.dailyButton)
    public void startDailyActivity(View view){

        Intent intent = new Intent(this, DailyForecastActivity.class);
        startActivity(intent);

    }

}



















