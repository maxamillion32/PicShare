package picshare.mk.com.picshare.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Surface;
import android.widget.ImageView;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import picshare.mk.com.picshare.Tabs.HomeTab;

import static com.google.android.gms.internal.zzip.runOnUiThread;

/**
 * Created by Malek on 4/3/2016.
 */
public class AppUtils {
    InputStream inputStream;
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

    public String getStringImage(Bitmap bmp) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        //byte[] imageBytes = baos.toByteArray();
        byte[] imageBytes = new byte[3000];

        String encodedImage = Base64.encodeObject(imageBytes, Base64.ENCODE);
        return encodedImage;
    }
    public int uploadFile(String sourceFileUri, String upLoadServerUri, final Context context, final String imagepath) {


        final ProgressDialog[] dialog = new ProgressDialog[1];
        runOnUiThread(new Runnable() {
            public void run() {
                dialog[0] = ProgressDialog.show(context, "", "Uploading file...", true);
            }
        });
        String fileName = sourceFileUri;
        int serverResponseCode = 0;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {



            Log.e("uploadFile", "Source File not exist :"+imagepath);

            runOnUiThread(new Runnable() {
                public void run() {
                    dialog[0].dismiss();
                }
            });

            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {
                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    +" F:/wamp/wamp/www/uploads";

                            Toast.makeText(context, "File Upload Complete.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        dialog[0].dismiss();
                        Toast.makeText(context, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        //dialog[0].dismiss();
                        Toast.makeText(context, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            runOnUiThread(new Runnable() {
                public void run() {
                   dialog[0].dismiss();

                }
            });

            return serverResponseCode;

        } // End else block
    }
}
