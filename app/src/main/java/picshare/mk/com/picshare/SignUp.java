package picshare.mk.com.picshare;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import picshare.mk.com.picshare.Utils.ImageUtils;

public class SignUp extends AppCompatActivity {
    private Button bOk, bCancel;
    private TextView name, prename, login, photoUrl, title,confirmPass, password;
    private Typeface font;
    private ImageView uploadPic;
    private UploadDialog dialog;
    private UploadDialog.customOnClickListener dialogListener;
    private int PICK_IMAGE_REQUEST = 1;
    private static Uri filePath;
    private static Bitmap bitmap=null;
    private ImageUtils img;
    private ImageView userPic;
    private String UPLOAD_URL ="http://meetbuddies.net16.net/images/users/upload.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        name = (TextView) findViewById(R.id.tname);
        prename = (TextView) findViewById(R.id.tprename);
        login = (TextView) findViewById(R.id.tlogin);
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
                    final String nom = name.getText().toString();
                    final String prenom = prename.getText().toString();
                    final String log = login.getText().toString();
                    final String pass = password.getText().toString();
                    final String photo ;

                    if(filePath!=null){
                        photo = "img_user_"+log;
                    }
                    else{
                        photo =null;
                    }
                   /* subTask = new SubscribeTask(nom, prenom, log, pass, photo, adr);
                    subTask.execute();*/

                }
            }

        });
    }
    public boolean verify() {
        if (name.getText().toString().equals("")) {
            name.setError("Empty Field Last Name");
            return false;
        }

        if (prename.getText().toString().equals("")) {
            prename.setError("Empty Field First Name");
            return false;
        }

        if (login.getText().toString().equals("")) {
            login.setError("Empty Field E-mail");
            return false;
        }
        if(android.util.Patterns.EMAIL_ADDRESS.matcher(login.getText()).matches()==false){
            login.setError("Invalid E-mail Format");
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
        name.setText("");
        prename.setText("");
        login.setText("");
        password.setText("");
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
}
