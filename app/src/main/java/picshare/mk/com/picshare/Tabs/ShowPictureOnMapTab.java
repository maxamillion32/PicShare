package picshare.mk.com.picshare.Tabs;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import picshare.mk.com.picshare.R;
import picshare.mk.com.picshare.Utils.AppUtils;
import picshare.mk.com.picshare.Utils.JSONParser;
import picshare.mk.com.picshare.Utils.MyGpsLocationListener;

public class ShowPictureOnMapTab extends AppCompatActivity {
    private GoogleMap googleMap;
    private AppUtils appU;
    private MyGpsLocationListener gps;
    private double latitude = 0;
    private double longitude = 0;
    private getPostsTask getPosts = null;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private Marker marker;
    private Hashtable<String, String> markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appU = new AppUtils();
        if (isNetworkAvailable()) {
            getPosts = new getPostsTask();
            getPosts.execute();
            setContentView(R.layout.activity_show_picture_on_map_tab);
            gps = new MyGpsLocationListener(ShowPictureOnMapTab.this);
            try {
                if (googleMap == null) {
                    googleMap = ((MapFragment) getFragmentManager().
                            findFragmentById(R.id.map)).getMap();
                }

                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                if (gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                } else {
                    gps.showSettingsAlert();
                    latitude = 45.5086699;
                    longitude = -73.5539925;
                }
                CameraUpdate center =
                        CameraUpdateFactory.newLatLng(new LatLng(latitude,
                                longitude));//Centring the map on My Locations
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(11);
                googleMap.moveCamera(center);
                googleMap.animateCamera(zoom);
                googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

                initImageLoader();
                markers = new Hashtable<String, String>();
                imageLoader = ImageLoader.getInstance();

                options = new DisplayImageOptions.Builder()
                        .showStubImage(R.drawable.loading)        //	Display Stub Image
                        .showImageForEmptyUri(R.drawable.loading)    //	If Empty image found
                        .cacheInMemory()
                        .cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ShowPictureOnMapTab.this);
            alertDialogBuilder.setMessage("Sorry There is no Internet Connection !! ");
            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    public class getPostsTask extends AsyncTask<String, String, String> {
        ArrayList<HashMap<String, String>> myPosts = new ArrayList<>();

        public getPostsTask() {
        }

        ;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
            JSONParser jParser = new JSONParser();
            JSONObject json = jParser.makeHttpRequest("http://picshare-android.esy.es/ws/getPosts.php", "GET", param);
            Log.i("response http", json.toString());
            try {
                int success = json.getInt("success");
                if (success == 1) {

                    JSONArray posts = json.getJSONArray("Posts");

                    for (int i = 0; i < posts.length(); i++) {
                        JSONObject post = posts.getJSONObject(i);
                        HashMap<String, String> postData = new HashMap<String, String>();
                        postData.put("user_id", post.getString("user_id"));
                        postData.put("location", post.getString("location"));
                        postData.put("likes", post.getString("likes"));
                        postData.put("title", post.getString("title"));
                        postData.put("date", post.getString("date"));
                        postData.put("image_url", post.getString("image_url"));
                        myPosts.add(postData);
                    }
                    return "success";
                } else {
                    //TODO Handle This case
                    return "fail";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String success) {
            super.onPostExecute(success);

            if (success.equals("success")) {
                LatLng position = new LatLng(latitude, longitude);
                for (int i = 0; i < myPosts.size(); i++) {
                    Map<String, Double> coor = appU.getCoordonatesFromString(myPosts.get(i).get("location"));
                    position = new LatLng(coor.get("latitude"), coor.get("longitude"));
                    final Marker marker = googleMap.addMarker(new MarkerOptions().position(position)
                            .title(myPosts.get(i).get("title")).snippet(myPosts.get(i).get("date")));
                    markers.put(marker.getId(), myPosts.get(i).get("image_url"));
                }
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(8), 2000, null);
            }
        }
    }

    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private View view;

        public CustomInfoWindowAdapter() {
            view = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            if (ShowPictureOnMapTab.this.marker != null
                    && ShowPictureOnMapTab.this.marker.isInfoWindowShown()) {
                ShowPictureOnMapTab.this.marker.hideInfoWindow();
                ShowPictureOnMapTab.this.marker.showInfoWindow();
            }
            return null;
        }

        @Override
        public View getInfoWindow(final Marker marker) {
            ShowPictureOnMapTab.this.marker = marker;
            String url = null;

            if (marker.getId() != null && markers != null && markers.size() > 0) {
                if (markers.get(marker.getId()) != null &&
                        markers.get(marker.getId()) != null) {
                    url = markers.get(marker.getId());

                }
            }
            final ImageView image = ((ImageView) view.findViewById(R.id.badge));

            if (url != null && !url.equalsIgnoreCase("null")
                    && !url.equalsIgnoreCase("")) {
                imageLoader.displayImage(url, image, options,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri,
                                                          View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view,
                                        loadedImage);

                                getInfoContents(marker);
                            }
                        });
            } else {
                image.setImageResource(R.drawable.loading);
            }

            final String title = marker.getTitle();
            final TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                titleUi.setText(title);
            } else {
                titleUi.setText("");
            }

            final String snippet = marker.getSnippet();
            final TextView snippetUi = ((TextView) view
                    .findViewById(R.id.snippet));
            if (snippet != null) {
                snippetUi.setText(snippet);
            } else {
                snippetUi.setText("");
            }

            return view;
        }
    }


    private void initImageLoader() {
        int memoryCacheSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            int memClass = ((ActivityManager)
                    getSystemService(Context.ACTIVITY_SERVICE))
                    .getMemoryClass();
            memoryCacheSize = (memClass / 8) * 1024 * 1024;
        } else {
            memoryCacheSize = 2 * 1024 * 1024;
        }

        final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this).threadPoolSize(5)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCacheSize(memoryCacheSize)
                .memoryCache(new FIFOLimitedMemoryCache(memoryCacheSize - 1000000))
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).enableLogging()
                .build();

        ImageLoader.getInstance().init(config);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
