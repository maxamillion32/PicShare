package picshare.mk.com.picshare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import picshare.mk.com.picshare.Utils.ImageUtils;
import picshare.mk.com.picshare.Utils.JSONParser;

public class SignUp extends AppCompatActivity {
    private Button bOk, bCancel;
    private TextView firstName, lastName, email, avatar,confirmPass, password;
    private Typeface font;
    private ImageView uploadPic;
    private UploadDialog dialog;
    private UploadDialog.customOnClickListener dialogListener;
    private int PICK_IMAGE_REQUEST = 1;
    private static Uri filePath;
    private static Bitmap bitmap=null;
    private ImageUtils img;
    private ImageView userPic;
    private SubscribeTask subTask = null;
    private String UPLOAD_URL ="http://picshare-android.esy.es/images/users/upload.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firstName = (TextView) findViewById(R.id.tprename);
        lastName = (TextView) findViewById(R.id.tname);
        email = (TextView) findViewById(R.id.tlogin);
        password = (TextView) findViewById(R.id.tpassword);
        confirmPass=(TextView) findViewById(R.id.tConfirmPassword);
        uploadPic = (ImageView) findViewById(R.id.upload_picture);
        bOk = (Button) findViewById(R.id.bok);
        this.setDialogButtonsClickBehavior();//Define Dialog buttons onCLickListener
        dialog = new UploadDialog(this,dialogListener);
        uploadPic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.show();
            }

        });
        bCancel = (Button) findViewById(R.id.bcancel);
        bCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }

        });
        bOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (verify()) {
                    final String nom = lastName.getText().toString();
                    final String prenom = firstName.getText().toString();
                    final String emailA = email.getText().toString();
                    final String pass = password.getText().toString();
                    final String photo ;

                    if(filePath!=null){
                        photo = "img_user_"+emailA;
                    }
                    else{
                        photo =null;
                    }
                  subTask = new SubscribeTask(nom, prenom, emailA, pass, photo);
                    subTask.execute();

                }
            }

        });
    }
    public boolean verify() {
        if (lastName.getText().toString().equals("")) {
            lastName.setError("Empty Field Last Name");
            return false;
        }

        if (firstName.getText().toString().equals("")) {
            firstName.setError("Empty Field First Name");
            return false;
        }

        if (email.getText().toString().equals("")) {
            email.setError("Empty Field E-mail");
            return false;
        }
        if(android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()==false){
            email.setError("Invalid E-mail Format");
            return false;
        }
        if (password.getText().toString().equals("")) {
            password.setError("Empty Field password");
            return false;
        }


        if(password.getText().toString().equals(confirmPass.getText().toString())==false){
            System.out.println(password.getText());
            System.out.println(confirmPass.getText());
            confirmPass.setError("Password do not match confirmation");
            return false;
        }
        return true;

    }
    public void clear() {
        firstName.setText("");
        lastName.setText("");
        email.setText("");
        password.setText("");
        confirmPass.setText("");
    }
    /**
     *display native file chooser to select an image
     */
    private void showFileChooser() {//Displaying native
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    /**
     *Take a new photo using device's Camera
     */
    private void uploadImage(){
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, 0);
    }
    private void setDialogButtonsClickBehavior(){
        dialogListener = new UploadDialog.customOnClickListener() {
            @Override
            public void onChooseButtonClick() {
                showFileChooser();
                dialog.dismiss();
            }

            @Override
            public void onUploadButtonClick() {
                uploadImage();
                dialog.dismiss();
            }
        };

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            //Toast.makeText(SignUp.this,filePath.toString(), Toast.LENGTH_SHORT).show();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                uploadPic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {//Take image using camera
            filePath = data.getData();
            //Toast.makeText(SignUp.this,filePath.toString(), Toast.LENGTH_SHORT).show();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                uploadPic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    public class SubscribeTask extends AsyncTask<String, String, String> {

        private ProgressDialog pdialog;
        private String msg;
        private final String firstName, lastName, email, mPass, avatar;

        public SubscribeTask(String firstName, String lastName, String email, String mPass, String avatar) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.mPass = mPass;
            this.avatar =avatar ;

    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        pdialog = new ProgressDialog(SignUp.this);
        pdialog.setMessage("Loading Please Wait ...");
        pdialog.show();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub

        ArrayList<NameValuePair> parames = new ArrayList<NameValuePair>();
        parames.add(new BasicNameValuePair("firstName", firstName));
        parames.add(new BasicNameValuePair("lastName", lastName));
        parames.add(new BasicNameValuePair("email", email));
        parames.add(new BasicNameValuePair("password", mPass));
        parames.add(new BasicNameValuePair("avatar_url", "http://picshare-android.esy.es/images/users/"+avatar+".png"));


        JSONParser jParser = new JSONParser();
        JSONObject json = jParser.makeHttpRequest("http://picshare-android.esy.es/ws/Subscribe.php", "GET", parames);


        Log.i("response http", json.toString());
        if(filePath!= null){

            Thread t= new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(avatar);
                    img= new ImageUtils(UPLOAD_URL,bitmap, avatar,SignUp.this);
                    img.uploadImage();
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        pdialog.dismiss();
        if (result.equals("success")) {

            Toast.makeText(SignUp.this, msg, Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {//Starting the LOgin activity after a delay to allow the profile picture to be uploaded
                @Override
                public void run() {
                    final Intent mainIntent = new Intent(SignUp.this, LoginActivity.class);
                    startActivity(mainIntent);
                }
            }, 8000);
        }
        if (result.equals("fail")) {

            Toast.makeText(SignUp.this, msg, Toast.LENGTH_LONG).show();

        }

        super.onPostExecute(result);
    }

}

}
