package picshare.mk.com.picshare.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

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

}
