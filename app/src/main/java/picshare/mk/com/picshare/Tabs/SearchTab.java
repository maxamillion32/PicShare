package picshare.mk.com.picshare.Tabs;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import picshare.mk.com.picshare.Post;
import picshare.mk.com.picshare.PostsAdapter;
import picshare.mk.com.picshare.R;
import picshare.mk.com.picshare.Utils.AppUtils;
import picshare.mk.com.picshare.Utils.GeocodeJSONParser;
import picshare.mk.com.picshare.Utils.GooglePlacesReadTask;
import picshare.mk.com.picshare.Utils.JSONParser;

public class SearchTab extends AppCompatActivity {

    Button mBtnFind;
    EditText etPlace;
    ListView mListView;
    AppUtils appUtils;
    private static final String GOOGLE_API_KEY = "AIzaSyA9-04GzkX4_1va1melL9mHnW5BDsrYAYc";
    private int PROXIMITY_RADIUS = 15000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tab);

        mListView = (ListView) findViewById(R.id.SearchPostsListView);
        appUtils = new AppUtils();

        // Getting reference to the find button
        mBtnFind = (Button) findViewById(R.id.btn_show);

        // Getting reference to EditText
        etPlace = (EditText) findViewById(R.id.et_place);

        // Setting click event listener for the find button
        mBtnFind.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Getting the place entered
                String location = etPlace.getText().toString();

                if (location == null || location.equals("")) {
                    Toast.makeText(getBaseContext(), "No Place is entered", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isNetworkAvailable()) {
                    ProgressDialog pdialog = new ProgressDialog(SearchTab.this);
                    pdialog.setMessage("Loading... Please Wait");
                    pdialog.show();

                    String url = "https://maps.googleapis.com/maps/api/geocode/json?";

                    try {
                        // encoding special characters like space in the user input place
                        location = URLEncoder.encode(location, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    String address = "address=" + location;

                    String sensor = "sensor=false";


                    // url , from where the geocoding data is fetched
                    url = url + address + "&" + sensor;

                    // Instantiating DownloadTask to get places from Google Geocoding service
                    // in a non-ui thread
                    DownloadTask downloadTask = new DownloadTask(pdialog);

                    // Start downloading the geocoding places
                    downloadTask.execute(url);

                } else {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SearchTab.this);
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
        });

    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);


            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }

        return data;

    }


    /**
     * A class, to download Places from Geocoding webservice
     */
    private class DownloadTask extends AsyncTask<String, Integer, String> {

        String data = null;
        ProgressDialog pdialog;

        public DownloadTask(ProgressDialog pdialog) {
            this.pdialog = pdialog;
        }

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {

            // Instantiating ParserTask which parses the json data from Geocoding webservice
            // in a non-ui thread
            ParserTask parserTask = new ParserTask(pdialog);

            // Start parsing the places in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }

    }

    /**
     * A class to parse the Geocoding Places in non-ui thread
     */
    class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;
        List<Post> posts = new ArrayList<Post>();
        ProgressDialog pdialog;

        public ParserTask(ProgressDialog pdialog) {
            this.pdialog = pdialog;
        }

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            GeocodeJSONParser parser = new GeocodeJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                /** Getting the parsed data as a an ArrayList */
                places = parser.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {

            for (int i = 0; i < list.size(); i++) {

                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(i);

                // Getting latitude of the place
                double lat = Double.parseDouble(hmPlace.get("lat"));

                // Getting longitude of the place
                double lng = Double.parseDouble(hmPlace.get("lng"));

                // Locate the first location
                if (i == 0) {
                    StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                    googlePlacesUrl.append("location=" + lat + "," + lng);
                    googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
                    googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);
                    googlePlacesUrl.append("&sensor=true");
                    GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
                    Object[] toPass = new Object[2];
                    toPass[1] = googlePlacesUrl.toString();
                    googlePlacesReadTask.execute(toPass);

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    double maxLat = googlePlacesReadTask.maxLat();
                    double minLat = googlePlacesReadTask.minLat();
                    double maxLng = googlePlacesReadTask.maxLng();
                    double minLng = googlePlacesReadTask.minLng();
                    SearchPhotoTask searchPhotoTask = new SearchPhotoTask(pdialog, maxLat, minLat, maxLng, minLng);
                    searchPhotoTask.execute();
                }
            }
        }
    }

    public class SearchPhotoTask extends AsyncTask<String, String, List<Post>> {

        double maxLat = 0, minLat = 0, maxLng = 0, minLng = 0;
        ProgressDialog pdialog;

        public SearchPhotoTask(ProgressDialog pdialog, double maxLat, double minLat, double maxLng, double minLng) {
            this.pdialog = pdialog;
            this.maxLat = maxLat;
            this.minLat = minLat;
            this.maxLng = maxLng;
            this.minLng = minLng;
        }

        @Override
        protected List<Post> doInBackground(String... params) {
            if (isNetworkAvailable()) {
                List<Post> posts = new ArrayList<Post>();
                ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

                JSONParser jParser = new JSONParser();
                JSONObject json = jParser.makeHttpRequest("http://picshare-android.esy.es/ws/getPosts.php", "GET", param);

                int success = 0;
                try {
                    success = json.getInt("success");
                    if (success == 1) {
                        JSONArray postsData = json.getJSONArray("Posts");
                        for (int i = 0; i < postsData.length(); i++) {
                            JSONObject postData = postsData.getJSONObject(i);
                            String userName = postData.getString("firstName") + " " + postData.getString("lastName");
                            String userAvatar = postData.getString("avatar_url");
                            String postId = postData.getString("post_id");
                            String title = postData.getString("title");
                            String picture = postData.getString("image_url");
                            String date = postData.getString("date");
                            String likes = postData.getString("likes");
                            String location = postData.getString("location");
                            String[] coord = location.split(",");
                            double latitude = Double.parseDouble(coord[0]);
                            double longitude = Double.parseDouble(coord[1]);
                            if ((latitude >= minLat) && (latitude <= maxLat)) {
                                posts.add(new Post(postId, title, userName, likes, picture, userAvatar, location, date));
                            }
                        }
                        if (posts.size() == 0) {
                            posts.add(new Post("", "No Posts", "", "", "", "", "", ""));
                        }
                        return posts;
                    } else {
                        System.out.println("Failure");
                        posts.add(new Post("", "No Posts", "", "", "", "", "", ""));
                        return posts;
                    }

                } catch (JSONException e) {
                    posts.add(new Post("", "No Posts", "", "", "", "", "", ""));
                    return posts;
                }
            } else {

                return null;
            }
        }

        protected void onPostExecute(List<Post> posts) {
            super.onPostExecute(posts);
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SearchTab.this);

            pdialog.dismiss();
            if (posts == null) {//No Internet connection
                alertDialogBuilder.setMessage("Sorry There is no Internet Connection !! ");
                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {
                if (posts.get(0).getTitle() == "No Posts") { //No Posts
                    alertDialogBuilder.setMessage("Sorry There are no Posts !! ");
                    alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            //  finish();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    PostsAdapter adapter = new PostsAdapter(SearchTab.this, posts);
                    mListView.setAdapter(adapter);
                }
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
