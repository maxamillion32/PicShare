package picshare.mk.com.picshare.Utils;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class PlacesDisplayTask extends AsyncTask<Object, Integer, List<HashMap<String, String>>> {

    JSONObject googlePlacesJson;
    double maxLat = 0, minLat = 0, maxLng = 0, minLng = 0;

    @Override
    protected List<HashMap<String, String>> doInBackground(Object... inputObj) {

        List<HashMap<String, String>> googlePlacesList = null;
        Places placeJsonParser = new Places();

        try {
            googlePlacesJson = new JSONObject((String) inputObj[1]);
            googlePlacesList = placeJsonParser.parse(googlePlacesJson);
        } catch (Exception e) {
        }
        return googlePlacesList;
    }

    @Override
    protected void onPostExecute(List<HashMap<String, String>> list) {

        maxLat = Double.parseDouble(list.get(0).get("lat"));
        minLat = Double.parseDouble(list.get(0).get("lat"));
        maxLng = Double.parseDouble(list.get(0).get("lng"));
        minLng = Double.parseDouble(list.get(0).get("lng"));
        for (int i = 0; i < list.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = list.get(i);
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
            LatLng latLng = new LatLng(lat, lng);
        }

    }

    public double getMaxLng() {
        return maxLng;
    }

    public double getMinLng() {
        return minLng;
    }

    public double getMaxLat() {
        return maxLat;
    }

    public double getMinLat() {
        return minLat;
    }
}

