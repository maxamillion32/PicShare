package picshare.mk.com.picshare.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Path;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Surface;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import picshare.mk.com.picshare.Tabs.HomeTab;

/**
 * Created by Malek on 4/3/2016.
 */
public class AppUtils {
    public AppUtils(){

    }
    public HashMap<String,Double> getCoordonatesFromString(String location){

        String[] coord=location.split(",");
        HashMap<String,Double> myCoord = new HashMap<>(2);
        myCoord.put("latitude",Double.parseDouble(coord[0]));
        myCoord.put("longitude", Double.parseDouble(coord[1]));
        return myCoord;
    }

    public void getImage(final ImageView imageView , final String picUrl) {
        System.out.println(picUrl);
        class GetImage extends AsyncTask<String,Void,Bitmap> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i("Download", "Profile Picture Downloaded");

            }

            @Override
            protected void onPostExecute(Bitmap b) {
                super.onPostExecute(b);
                imageView.setImageBitmap(b);
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                URL url = null;
                Bitmap image = null;
                Bitmap finalImage=null;
                try {
                    url = new URL(picUrl);
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    finalImage= resize(image,120,120);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return finalImage;
            }
        }

        GetImage gi = new GetImage();
        gi.execute();
    }
    public Bitmap resize(Bitmap bm, int w, int h)
    {
        int width = bm.getWidth();
        int height = bm.getHeight();
        int newWidth = w;
        int newHeight = h;
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);

        return resizedBitmap;
    }
    public void noInternetConnection(Boolean internetStatus, final Context c){
    if(internetStatus==false){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
        alertDialogBuilder.setMessage("Sorry There is no Internet Connection");

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                c.startActivity(intent);
            }
        });
        //Toast.makeText(c, "msg msg", Toast.LENGTH_LONG).show();
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

}
