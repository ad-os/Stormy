package com.example.android.stormy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private CurrentWeather mCurrentWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String apiKey = "da6e8c57e760581f8262058f6a28c70c";
        double latitude = 37.8267;
        double longitude = -122.423;

        String forecastUrl = "https://api.forecast.io/forecast/" + apiKey +
                "/" + latitude + "," + longitude;

        if(isNetworkConnected()) {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().
                    url(forecastUrl).
                    build();

            Call call = client.newCall(request);


            call.enqueue(new Callback() {

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.e(TAG, "Exception caught : ", e);
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mCurrentWeather = getCurrentDetails(jsonData);
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

        Log.v(TAG, "Executing main thread");
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



















