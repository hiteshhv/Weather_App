package com.example.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class MainActivity extends AppCompatActivity {
    LocationManager locationManager;
    LocationListener locationListener;

    EditText editText;
    TextView description;
    Button button;
    TextView tempratureViewField;
    TextView humidity;
    TextView feelsLike;
    TextView temp_max;
    TextView temp_min;
    TextView weatherIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        description = findViewById(R.id.description);
        tempratureViewField = findViewById(R.id.tempratureViewField);
        humidity = findViewById(R.id.humidity);
        feelsLike = findViewById(R.id.feelsLike);
        temp_max = findViewById(R.id.temp_max);
        temp_min = findViewById(R.id.temp_min);
        weatherIcon = findViewById(R.id.weatherIcon);
        editText.setHint("Enter Location...");

      button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWeather(view);
            }
        });

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                TextView lat = findViewById(R.id.lat);
                TextView lng = findViewById(R.id.lng);
                lat.setText(Double.toString(location.getLatitude()));
                lng.setText(Double.toString(location.getLongitude()));

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (lastKnownLocation != null) {
                updateLocationInfo(lastKnownLocation);
            }

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startListening();
        }
    }
    public void startListening(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }

    private void updateLocationInfo(Location lastKnownLocation) {
    }


    public void getWeather(View v) {
        try {
            DownloadTask task = new DownloadTask();
            String encodedCityName = URLEncoder.encode(editText.getText().toString(), "UTF-8");

            task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=d5ac30ece884f8f5feee41d8800dc74e");
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Could not find weather :(",Toast.LENGTH_SHORT).show();
        }
    }


    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);

                String weatherInfo = jsonObject.getString("weather");

                JSONArray arr = new JSONArray(weatherInfo);

                String message = "";

                for (int i=0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");

                   try {
                       String clear = "clear sky";
                       String clouds = "few clouds";
                       String brokenClouds = "broken clouds";
                       String scatteredClouds = "scattered clouds";
                       String snow = "snow";
                       String lightsnow = "light snow";
                       String mist = "mist";
                       String haze = "haze";
                       String smoke = "smoke";
                       String lightrain = "light rain";
                       String rain = "rain";

                       if (clear.equals(description)) {
                           weatherIcon.setBackgroundResource(R.drawable.sunnyone);
                       }

                       if (clouds.equals(description)) {
                           weatherIcon.setBackgroundResource(R.drawable.fewcloudsone);
                       }
                       if (brokenClouds.equals(description)) {
                           weatherIcon.setBackgroundResource(R.drawable.fewcloudsone);
                       }
                       if (scatteredClouds.equals(description)) {
                           weatherIcon.setBackgroundResource(R.drawable.fewcloudsone);
                       }
                       if (snow.equals(description) || lightsnow.equals(description)) {
                           weatherIcon.setBackgroundResource(R.drawable.snowone);
                       }
                       if (mist.equals(description) || haze.equals(description) || smoke.equals(description)) {
                           weatherIcon.setBackgroundResource(R.drawable.hazeone);
                       }
                       if (rain.equals(description) || lightrain.equals(description)) {
                           weatherIcon.setBackgroundResource(R.drawable.rainone);
                       }
                   }
                   catch (Exception e){
                       e.printStackTrace();
                   }



                    if (!main.equals("") && !description.equals("")) {
                        message += description + "\r\n";

                    }
                }

                if (!message.equals("")) {
                    description.setText(message);
                } else {
                    Toast.makeText(getApplicationContext(),"Could not find weather :(",Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {

                Toast.makeText(getApplicationContext(),"Could not find weather :(",Toast.LENGTH_SHORT).show();

                e.printStackTrace();
            }
            try {
                JSONObject jsonObj = new JSONObject(s);

                JSONObject main = jsonObj.getJSONObject("main");
                String temp = main.getString("temp");
                double tempStr = Double.parseDouble(temp);
                int tempInt = (int)tempStr;
                String currentTemp = Integer.toString(tempInt);
                String tempDisp = currentTemp + "°";
                tempratureViewField.setText(tempDisp);

                String humidityValue = main.getString("humidity");
                String humidityDisp = "humidity:" + humidityValue;
                humidity.setText(humidityDisp);

                String feelsLikeTempValue = main.getString("feels_like") + "°C";
                String feelsLikeTempDisp = "Feels Like:" + feelsLikeTempValue;
                feelsLike.setText(feelsLikeTempDisp);

                String maxTempValue = main.getString("temp_max") + "°";
                String maxTempDisp = "Max:" + maxTempValue;
                temp_max.setText(maxTempDisp);

                String minTempValue = main.getString("temp_min") + "°";
                String minTempDisp = "Min:" + minTempValue;
                temp_min.setText(minTempDisp);


            } catch (Exception e) {

                Toast.makeText(getApplicationContext(),"Could not find weather :(",Toast.LENGTH_SHORT).show();

                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_1:
                openAboutActivity();
                break;
            default: super.onOptionsItemSelected(item);
        }
        return true;
    }
    public void openAboutActivity(){
        Intent intent = new Intent(this, AboutMenuActivity.class);
        startActivity(intent);
    }
}


