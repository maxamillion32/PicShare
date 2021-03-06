package picshare.mk.com.picshare.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;



/**
 * Created by Malek on 4/6/2016.
 */
public class ImageUtils {
    private Bitmap bitmap;

    private int PICK_IMAGE_REQUEST = 1;

    private String UPLOAD_URL =null;

    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";
    private String name;
    AppCompatActivity targetActivity;
    public ImageUtils(String UPLOAD_URL, Bitmap bitmap, String name, AppCompatActivity c){
        this.UPLOAD_URL=UPLOAD_URL;
        this.bitmap=bitmap;
        this.name=name;
        this.targetActivity=c;

    }
    public ImageUtils(){}
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        //byte[] imageBytes = baos.toByteArray();
        byte[] imageBytes = new byte[3000];

        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    public void uploadImage(){
        //Showing the progress dialog
        Bitmap imggBitmap=this.bitmap;
        final String fileName=this.name;
        final ProgressDialog[] loading = {null};
        targetActivity.runOnUiThread(new Runnable() {
            public void run() {
                loading[0] = ProgressDialog.show(targetActivity, "Uploading...", "Please wait...", false, false);
            }
        });

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String s) {
                        //Disimissing the progress dialog

                        targetActivity.runOnUiThread(new Runnable() {
                            public void run() {
                                loading[0].dismiss();

                                //Showing toast message of the response
                                Toast.makeText(targetActivity,s, Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError volleyError) {
                        targetActivity.runOnUiThread(new Runnable() {
                            public void run() {
                                //Dismissing the progress dialog
                                loading[0].dismiss();

                                //Showing toast
                                // Toast.makeText(targetActivity, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String

                String image = encodeTobase64(bitmap);//getStringImage(bitmap);

                //Getting Image Name
                String name= fileName;

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_IMAGE, image);
                params.put(KEY_NAME, name);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(targetActivity);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }
    public Bitmap getImage(String imgUrl) {
        final Bitmap[] remoteImg = new Bitmap[1];
        class GetImage extends AsyncTask<String,Void,Bitmap> {
            //ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // loading = ProgressDialog.show(ViewImage.this, "Uploading...", null,true,true);
            }

            @Override
            protected void onPostExecute(Bitmap b) {
                super.onPostExecute(b);
                //  loading.dismiss();
                //   imageView.setImageBitmap(b);
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                System.out.println("Downloading");
                String imageUrl = params[0];
                URL url = null;
                Bitmap image = null;
                try {
                    url = new URL(imageUrl);
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    remoteImg[0] =image;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return image;
            }
        }

        GetImage gi = new GetImage();
        gi.execute(imgUrl);
        return remoteImg[0];
    }
    public static String encodeTobase64(Bitmap image) {
        int initilaWidth = image.getWidth();
        int initialHeight=image.getHeight();

        if(initialHeight>2500 || initialHeight>2500){ //Editing image size in order to compress it and reduice memory usage
            image=getScaledBitmap(image,initilaWidth/3,initialHeight/3);
        }else{
            if(initialHeight>1200 || initialHeight>1200){
                image=getScaledBitmap(image,initilaWidth/2,initialHeight/2);
            }
        }

        image.getWidth();
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte [] b=baos.toByteArray();
        String temp=null;
        try{
            System.gc();
            temp=Base64.encodeToString(b, Base64.DEFAULT);
        }catch(Exception e){
            e.printStackTrace();
        }catch(OutOfMemoryError e){
            baos=new  ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG,50, baos);
            b=baos.toByteArray();
            temp=Base64.encodeToString(b, Base64.DEFAULT);
            Log.e("EWN", "Out of memory error catched");
        }
        return temp;
    }
    public static Bitmap getScaledBitmap(Bitmap bitmap, int newWidth, int newHeight)
    {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // RECREATE THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

}

