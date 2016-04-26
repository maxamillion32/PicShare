package picshare.mk.com.picshare.Tabs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import picshare.mk.com.picshare.R;
import picshare.mk.com.picshare.Utils.AppUtils;
import picshare.mk.com.picshare.Utils.JSONParser;
import picshare.mk.com.picshare.Utils.MyGpsLocationListener;
import picshare.mk.com.picshare.Utils.SessionManager;

public class PictureTab extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private NewPostTask postTask;
    private Sensor mPressure;
    private SessionManager session;
    private SmartImageView picture;
    private AppUtils appU;
    private Button postButton;
    private Bitmap bitmap;
    private Uri filePath;
    private String imagepath = null;
    private EditText title;
    private MyGpsLocationListener gps;
    private String currentLocation, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_tab);
        if (isNetworkAvailable()) {
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            picture = (SmartImageView) findViewById(R.id.takenPicture);
            title = (EditText) findViewById(R.id.post_title);
            postButton = (Button) findViewById(R.id.buttonPost);
            session = new SessionManager(PictureTab.this);
            appU = new AppUtils();
            gps = new MyGpsLocationListener(PictureTab.this);
            setDateAndLocation();
            takePicture();
            postButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userId = session.getId() + "";
                    String postTitle = title.getText().toString();

                    Random r = new Random();
                    String image_name = "post_" + r.nextInt(8000);
                    postTask = new NewPostTask(postTitle, userId, currentLocation, date, image_name);
                    postTask.execute();
                }
            });
        } else {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PictureTab.this);
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values[0] == 0.0) {
            takePicture();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        mSensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL);


    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {//Take image using camera
            filePath = data.getData();

            Uri selectedImageUri = data.getData();
            imagepath = getPath(selectedImageUri);
            Bitmap bitmap = BitmapFactory.decodeFile(imagepath);
            ExifInterface ei = null;
            try { //Fixing Image Orientation Issue; Checking the screen orientation then we rotate the ImageView
                ei = new ExifInterface(imagepath);
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        bitmap = appU.rotateImage(bitmap, 270);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        bitmap = appU.rotateImage(bitmap, 180);
                        break;
                    // etc.
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            picture.setImageBitmap(bitmap);

            // picture.setImageBitmap(bitmap);

            //picture.setImageUrl(imagepath);
            // picture.setImageBitmap();

        }


    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void takePicture() {

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, 0);
    }

    public class NewPostTask extends AsyncTask<String, String, String> {
        private ProgressDialog pdialog;
        private String msg;
        private final String title, user, location, date, image;

        public NewPostTask(String title, String user, String location, String date, String image) {
            this.title = title;
            this.user = user;
            this.location = location;
            this.date = date;
            this.image = image;

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            pdialog = new ProgressDialog(PictureTab.this);
            pdialog.setMessage("Loading Please Wait ...");
            pdialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("location", location));
            parameters.add(new BasicNameValuePair("title", title));
            parameters.add(new BasicNameValuePair("date", date));
            parameters.add(new BasicNameValuePair("user_id", user));
            String path = imagepath.substring(imagepath.lastIndexOf("/"), imagepath.length());
            parameters.add(new BasicNameValuePair("image_url", "http://meetbuddies.net16.net/images/posts/uploads" + path));
            JSONParser jParser = new JSONParser();
            JSONObject json = jParser.makeHttpRequest("http://picshare-android.esy.es/ws/addPost.php", "GET", parameters);
            Log.i("response http", json.toString());
            if (filePath != null) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        appU.uploadFile(imagepath, "http://meetbuddies.net16.net/images/posts/uploadImg.php", PictureTab.this, imagepath);

                    }
                });
                t.start();
            }
            try {
                synchronized (this) {
                    wait(8000);
                    int success = json.getInt("success");

                    if (success == 1) {
                        msg = json.getString("message");

                        return "success";

                    } else {
                        msg = json.getString("message");
                        return "fail";

                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            pdialog.dismiss();
            if (result.equals("success")) {
                Toast.makeText(PictureTab.this, msg, Toast.LENGTH_LONG).show();
            }
            if (result.equals("fail")) {

                Toast.makeText(PictureTab.this, "Error posting Image", Toast.LENGTH_LONG).show();

            }
        }
    }

    private void setDateAndLocation() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        date = df.format(c.getTime());
        if (gps.canGetLocation()) {
            currentLocation = gps.getLatitude() + "," + gps.getLongitude();
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
