package com.example.panacea;


import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class RecommendRestaurant {

    private static final String TAG = "RestaurantRecommendation";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place/nearbysearch";
    private static final String TYPE_RESTAURANT = "restaurant";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyC_wzATn9BoVznZ3A_QHhBRUznjLnQ2f-o";

    public boolean canThisRestaurantBeRecommended(String name, String lat, String lng) {
        LatLng restaurantLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

        PlacesTask placesTask = new PlacesTask();
        String restaurantName = name.replaceAll("\\s", "+");
        placesTask.execute(restaurantName, String.valueOf(restaurantLocation.latitude),
                String.valueOf(restaurantLocation.longitude));


        try {
            String result = placesTask.get();

            JSONObject jsonObject = new JSONObject(result);
            JSONArray results = jsonObject.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject place = results.getJSONObject(i);
                double rating = place.optDouble("rating", 0.0);

                if (rating >= 4.0) {
                    // Perform additional actions, e.g., show recommendation to the user
                    Log.d(TAG, "Recommended Restaurant: " + place.getString("name") +
                            ", Rating: " + rating);
                    return true; // Restaurant can be recommended
                }
            }

        } catch (JSONException | InterruptedException e) {
            Log.e(TAG, "Error processing Places API response");
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        return false; // No suitable restaurant found
    }

    private static class PlacesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String result = null;

            try {
                String urlStr = PLACES_API_BASE + OUT_JSON +
                        "?name=" + params[0] +
                        "&location=" + params[1] + "," + params[2] +
                        "&radius=1000" + // Specify your desired radius
                        "&type=" + TYPE_RESTAURANT +
                        "&key=" + API_KEY;

                URL url = new URL(urlStr);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                StringBuilder buffer = new StringBuilder();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                result = buffer.toString();

            } catch (MalformedURLException e) {
                Log.e(TAG, "Error processing Places API URL", e);
            } catch (IOException e) {
                Log.e(TAG, "Error connecting to Places API", e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error closing reader", e);
                    }
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray results = jsonObject.getJSONArray("results");

            } catch (JSONException e) {
                Log.e(TAG, "Error parsing Places API response", e);
            }
        }
    }
}

