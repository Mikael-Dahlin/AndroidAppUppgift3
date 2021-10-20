package com.example.lifecycle;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 *
 * @author Mikael-Dahlin
 * Class that handles the API calls and response.
 */
public class ApiHandler {

    // Constuctor.
    public ApiHandler() {
    }

    /*
     * Method that tests if the city could be found in the API and returns a String response.
     * If successful takes the coordinates from the response and sends it to the next API call.
     */
    public void getWeather(String city, Context context, TextView weatherInfo) {
        // Get settings for the weather.
        SharedPreferences sharedPreferences = context.getSharedPreferences("appPreferences", Context.MODE_PRIVATE);
        String wd = sharedPreferences.getString("weatherData", "none");
        Log.d("weather", "getWeather: ");
        if (!(wd.isEmpty() || wd.equals("none"))) {
            Log.d("weather", "getWeather: in if ");
            String[] weatherTitles = wd.split("\n");

            // Generate the url.
            String url = "http://api.openweathermap.org/data/2.5/weather?q=" +
                    city.toLowerCase() + ",se&units=metric&APPID=034e4427ae8ee2ed99f6d8fcf3deca16&mode=json";

            Log.d("weather", "getWeather: " + url);
            // Send API request with Volley.
            RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());

            Log.d("weather", "getWeather: " + weatherTitles.toString() + " length: " + weatherTitles.length);
            JsonObjectRequest JsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("weather", "onResponse: " + response);
                            String weatherString = "";
                            try {
                                for (int i = 1; i < weatherTitles.length; i++) {
                                    Log.d("weather", "onResponse: " + weatherTitles[i].toLowerCase().toString());
                                    if (weatherTitles[i].toLowerCase().equals("description")){
                                        JSONArray weather = response.getJSONArray("weather");

                                        for (int j = 0; j < weather.length(); j++) {
                                            JSONObject weatherJSONObject = weather.getJSONObject(j);
                                            weatherString += weatherTitles[i]+ ": " + weatherJSONObject.getString(weatherTitles[i].toLowerCase()) + "\n";
                                        }

                                    } else if (weatherTitles[i].toLowerCase().equals("wind speed")){
                                        String[] windSpeed = weatherTitles[i].toLowerCase().split(" ");
                                        JSONObject windJSONObject = response.getJSONObject(windSpeed[0]);
                                        weatherString += weatherTitles[i]+ ": " + windJSONObject.getString(windSpeed[1]) + "m/s\n";
                                    } else if (weatherTitles[i].toLowerCase().equals("temperature")){
                                        JSONObject tempJSONObject = response.getJSONObject("main");
                                        weatherString += weatherTitles[i]+ ": " + tempJSONObject.getString("temp") + "degrees celsius\n";
                                    }
                                }
                                weatherInfo.setText(weatherString);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    weatherInfo.setText("That didn't work!");
                }
            });



            queue.add(JsonRequest);
        }
    }
}