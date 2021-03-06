package picshare.mk.com.picshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import picshare.mk.com.picshare.Utils.DataBaseConnector;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);//Getting ActionBar Object
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        DataBaseConnector db = new DataBaseConnector(SplashScreen.this);
        if ((db.verifyTable()) && (db.getAllUsers().getCount() > 0)) {
            // System.out.println("User connected");
            startActivity(new Intent(SplashScreen.this, MainActivity.class));//Main
        } else {
            getSupportActionBar().hide();//Hiding ActionBar
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


            setContentView(R.layout.activity_splash_screen);
        }
    }

    public void goToLogin(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void goToSignUp(View view) {
        startActivity(new Intent(this, SignUp.class));
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}

