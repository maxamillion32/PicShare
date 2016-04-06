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
    Activity targetActivity;
    public ImageUtils(String UPLOAD_URL, Bitmap bitmap, String name, Activity c){
        this.UPLOAD_URL=UPLOAD_URL;
        this.bitmap=bitmap;
         this.name=name;
        this.targetActivity=c;

    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void uploadImage(){
        final ProgressDialog[] loading = {null};
        targetActivity.runOnUiThread(new Runnable() {
            public void run() {
                loading[0] = ProgressDialog.show(targetActivity, "Uploading...", "Please wait...", false, false);
            }
        });
        StringRequest stringRequest = new StringRequest(Request.Method.GET, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        targetActivity.runOnUiThread(new Runnable() {
                            public void run() {
                                loading[0].dismiss();

                                //Showing toast message of the response
                                Toast.makeText(targetActivity,"Upload", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        targetActivity.runOnUiThread(new Runnable() {
                            public void run() {
                                //Dismissing the progress dialog
                                loading[0].dismiss();

                                //Showing toast
                           //  Toast.makeText(targetActivity, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                            }
                        });;
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);

                //Getting Image Name
                String name = getName();

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
private String getName(){
    return name;
}
}

