package picshare.mk.com.picshare.Tabs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import picshare.mk.com.picshare.R;
import picshare.mk.com.picshare.Utils.MyGpsLocationListener;

public class ShowPictureOnMapTab extends AppCompatActivity {
    private GoogleMap googleMap;
    private MyGpsLocationListener gps;
    private  double latitude = 0;
    private double longitude = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_picture_on_map_tab);
        gps = new MyGpsLocationListener(ShowPictureOnMapTab.this);
        try {
            if (googleMap == null) {
                googleMap = ((MapFragment) getFragmentManager().
                        findFragmentById(R.id.map)).getMap();
            }

            googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            if (gps.canGetLocation()) {
                latitude=gps.getLatitude();
                longitude=gps.getLongitude();
            } else {
                gps.showSettingsAlert();
                latitude=45.5086699;
                longitude=-73.5539925;
            }
            CameraUpdate center=
                    CameraUpdateFactory.newLatLng(new LatLng(latitude,
                            longitude));//Centring the map on My Locations
            CameraUpdate zoom=CameraUpdateFactory.zoomTo(11);
            googleMap.moveCamera(center);
            googleMap.animateCamera(zoom);
            //TODO Add Markers Dynalically + add Popup when a pin is selected
            Marker dr1 = googleMap.addMarker(new MarkerOptions().
                    position(new LatLng(latitude , longitude)).title("Me"));
            Marker dr2 = googleMap.addMarker(new MarkerOptions().
                    position(new LatLng(45.497850 , -73.570547)).title("John Doe"));
            Marker dr3 = googleMap.addMarker(new MarkerOptions().
                    position(new LatLng(45.519744 , -73.475962)).title("John Doe"));
            Marker dr4 = googleMap.addMarker(new MarkerOptions().
                    position(new LatLng(45.526720 , -73.534670)).title("John Doe"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
