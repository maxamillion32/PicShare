package picshare.mk.com.picshare.Utils;

import android.os.AsyncTask;

public class GooglePlacesReadTask extends AsyncTask<Object, Integer, String> {
    String googlePlacesData = null;
    PlacesDisplayTask placesDisplayTask = null;

    @Override
    protected String doInBackground(Object... inputObj) {
        try {
            String googlePlacesUrl = (String) inputObj[1];
            Http http = new Http();
            googlePlacesData = http.read(googlePlacesUrl);
        } catch (Exception e) {
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        placesDisplayTask = new PlacesDisplayTask();
        Object[] toPass = new Object[2];
        toPass[1] = result;
        placesDisplayTask.execute(toPass);
    }

    public double maxLng() {
        return placesDisplayTask.getMaxLng();
    }

    public double minLng() {
        return placesDisplayTask.getMinLng();
    }

    public double maxLat() {
        return placesDisplayTask.getMaxLat();
    }

    public double minLat() {
        return placesDisplayTask.getMinLat();
    }
}