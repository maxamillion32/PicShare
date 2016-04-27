package picshare.mk.com.picshare;

import android.app.TabActivity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

import picshare.mk.com.picshare.Tabs.HomeTab;
import picshare.mk.com.picshare.Tabs.PictureTab;
import picshare.mk.com.picshare.Tabs.ProfileTab;
import picshare.mk.com.picshare.Tabs.SearchTab;
import picshare.mk.com.picshare.Tabs.ShowPictureOnMapTab;

public class MainActivity extends TabActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private long lastUpdate;
    private boolean move = false;
    private TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTabs();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();
    }

    private void setTabs() {

        addTab("Home", R.drawable.home, new Intent().setClass(this, HomeTab.class));
        addTab("Search ", R.drawable.search, new Intent().setClass(this, SearchTab.class));
        addTab("Photo", R.drawable.cam, new Intent().setClass(this, PictureTab.class));
        addTab("Show", R.drawable.where, new Intent().setClass(this, ShowPictureOnMapTab.class));
        addTab("Profile", R.drawable.profile, new Intent().setClass(this, ProfileTab.class));


    }

    private void addTab(String labelId, int drawableId, Intent intent2) {
        tabHost = getTabHost();
        Intent intent = new Intent(intent2);
        TabHost.TabSpec spec = tabHost.newTabSpec(labelId);

        View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);
        ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
        icon.setImageResource(drawableId);

        spec.setIndicator(tabIndicator);
        spec.setContent(intent);
        tabHost.addTab(spec);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_energy:
                Intent intent = new Intent(this, BatteryActivity.class);
                this.startActivity(intent);
                break;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;
        if (accelationSquareRoot >= 2) //
        {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;

            if (move) {// SHake the Phone Two times
                int currentTab = tabHost.getCurrentTab();
                if (currentTab >= 0 && currentTab < 4) {
                    tabHost.setCurrentTab(currentTab + 1);
                } else {
                    if (currentTab == 4) {
                        tabHost.setCurrentTab(0);
                    }
                }

            }
            move = !move;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }
}
