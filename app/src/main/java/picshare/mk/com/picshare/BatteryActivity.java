package picshare.mk.com.picshare;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.TextView;

import picshare.mk.com.picshare.Utils.EnergyConsumptionUtils;


public class BatteryActivity extends Activity {

    TextView batLevel, technology, plugged, health, status, voltage, temperature, cpu, memory;
    String batteryLevelInfo = "Battery Level";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);

        batLevel = (TextView) findViewById(R.id.batteryLevel);
        technology = (TextView) findViewById(R.id.technology);
        plugged = (TextView) findViewById(R.id.plugged);
        health = (TextView) findViewById(R.id.health);
        status = (TextView) findViewById(R.id.status);
        voltage = (TextView) findViewById(R.id.voltage);
        temperature = (TextView) findViewById(R.id.temperature);
        cpu = (TextView) findViewById(R.id.cpu);
        memory = (TextView) findViewById(R.id.memory);

        registerBatteryLevelReceiver();

        final EnergyConsumptionUtils energyUtils = new EnergyConsumptionUtils(this);
        //  battery = (TextView) view.findViewById(R.id.battery);
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String cpuUse = Float.toString(energyUtils.readUsage());
                                String memUsage = Long.toString(energyUtils.getMemoryUsage());
                                String memSize = Long.toString(energyUtils.getUsedMemorySize());
                                String memUse = memSize + "/" + memUsage;
                                //   String batLevel = Float.toHexString(energyUtils.getBatteryLevel(getContext()));
                                cpu.setText(cpuUse + " %");
                                memory.setText(memUse);
                                //  battery.setText(batLevel);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(battery_receiver);

        super.onDestroy();
    }

    private BroadcastReceiver battery_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isPresent = intent.getBooleanExtra("present", false);
            String tech = intent.getStringExtra("technology");
            int plug = intent.getIntExtra("plugged", -1);
            int scale = intent.getIntExtra("scale", -1);
            int hea = intent.getIntExtra("health", 0);
            int sta = intent.getIntExtra("status", 0);
            int rawlevel = intent.getIntExtra("level", -1);
            int volt = intent.getIntExtra("voltage", 0);
            int temp = intent.getIntExtra("temperature", 0);
            int level = 0;

            if (isPresent) {
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }

                batLevel.setText("" + level + "%");
                technology.setText(tech);
                plugged.setText(getPlugTypeString(plug));
                health.setText(getHealthString(hea));
                status.setText(getStatusString(sta));
                voltage.setText("" + volt);
                temperature.setText("" + temp);

            } else {
            }
        }
    };

    private String getPlugTypeString(int plugged) {
        String plugType = "Unknown";

        switch (plugged) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                plugType = "AC";
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                plugType = "USB";
                break;
        }

        return plugType;
    }

    private String getHealthString(int health) {
        String healthString = "Unknown";

        switch (health) {
            case BatteryManager.BATTERY_HEALTH_DEAD:
                healthString = "Dead";
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                healthString = "Good";
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                healthString = "Over Voltage";
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                healthString = "Over Heat";
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                healthString = "Failure";
                break;
        }

        return healthString;
    }

    private String getStatusString(int status) {
        String statusString = "Unknown";

        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                statusString = "Charging";
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                statusString = "Discharging";
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                statusString = "Full";
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                statusString = "Not Charging";
                break;
        }

        return statusString;
    }

    private void registerBatteryLevelReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        registerReceiver(battery_receiver, filter);
    }
}


