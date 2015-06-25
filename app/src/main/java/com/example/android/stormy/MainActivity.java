package com.example.android.stormy;

import android.app.Fragment;
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

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private CurrentWeather mCurrentWeather;
    private double mCurrentLatitude;
    private double mCurrentLongitude;

    //@function is called as annotation as it expands the one line of code.
    @InjectView(R.id.timeLabel) TextView mTimeLabel;
    @InjectView(R.id.temperatureLabel) TextView mTemperatureLabel;
    @InjectView(R.id.humidityValue) TextView mHumidityValue;
    @InjectView(R.id.precipValue) TextView mPrecipValue;
    @InjectView(R.id.summaryLabel) TextView mSummaryLabel;
    @InjectView(R.id.iconImageView) ImageView mIconImageView;
    @InjectView(R.id.refreshImageView) ImageView mRefreshImageView;
    @InjectView(R.id.progressBar) ProgressBar mProgressBar;

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
                "/" + latitude + "," + longitude + "?units=si" +
                "";

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
                            mCurrentWeather = getCurrentDetails(jsonData);
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
        mTemperatureLabel.setText(mCurrentWeather.getTemperature() + "");
        mTimeLabel.setText("At " + mCurrentWeather.getFormattedTime() + " it will be");
        mHumidityValue.setText(mCurrentWeather.getHumidity() + "");
        mPrecipValue.setText(mCurrentWeather.getPrecipChance() + "%");
        mSummaryLabel.setText(mCurrentWeather.getSummary());


        Drawable drawable = getResources().getDrawable(mCurrentWeather.getIconId());
        mIconImageView.setImageDrawable(drawable);
    }

    //here with throws key word we are passing the responsibility to whoever calls this method.
    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject currently = forecast.getJSONObject("currently");

        CurrentWeather currentWeather = new CurrentWeather();

        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setPrecipChance(currently.getInt("precipProbability"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setTimeZone(timezone);
        Log.i(TAG, "From Json" + timezone);

        return currentWeather;
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

}



















