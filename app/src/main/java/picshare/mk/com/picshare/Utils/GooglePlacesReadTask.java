package picshare.mk.com.picshare.Utils;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class GooglePlacesReadTask extends AsyncTask<Object, Integer, String> {
    String googlePlacesData = null;
    //PlacesDisplayTask placesDisplayTask = null;
    JSONObject googlePlacesJson;
    double maxLat = 0, minLat = 0, maxLng = 0, minLng = 0;

    @Override
    protected String doInBackground(Object... inputObj) {
        try {
            String googlePlacesUrl = (String) inputObj[1];
            Http http = new Http();
            googlePlacesData = http.read(googlePlacesUrl);
        } catch (Exception e) {
        }
        //placesDisplayTask = new PlacesDisplayTask();
        //Object[] toPass = new Object[2];
        //toPass[1] = googlePlacesData;
        List<HashMap<String, String>> googlePlacesList = null;
        Places placeJsonParser = new Places();

        try {
            googlePlacesJson = new JSONObject(googlePlacesData);
            googlePlacesList = placeJsonParser.parse(googlePlacesJson);
        } catch (Exception e) {
        }
        if (googlePlacesList.size() > 0) {
            maxLat = Double.parseDouble(googlePlacesList.get(0).get("lat"));
            minLat = Double.parseDouble(googlePlacesList.get(0).get("lat"));
            maxLng = Double.parseDouble(googlePlacesList.get(0).get("lng"));
            minLng = Double.parseDouble(googlePlacesList.get(0).get("lng"));
            for (int i = 0; i < googlePlacesList.size(); i++) {
                HashMap<String, String> googlePlace = googlePlacesList.get(i);
                double lat = Double.parseDouble(googlePlace.get("lat"));

                if (lat > maxLat) {
                    maxLat = lat;
                }
                if (lat < minLat) {
                    minLat = lat;
                }
                double lng = Double.parseDouble(googlePlace.get("lng"));
                if (lng > maxLat) {
                    maxLng = lng;
                }
                if (lng < minLat) {
                    minLng = lng;
                }
            }
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
    }

    public double maxLng() {
        return maxLng;
    }

    public double minLng() {
        return minLng;
    }

    public double maxLat() {
        return maxLat;
    }

    public double minLat() {
        return minLat;
    }
}