package picshare.mk.com.picshare;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

import picshare.mk.com.picshare.Tabs.HomeTab;
import picshare.mk.com.picshare.Tabs.PictureTab;
import picshare.mk.com.picshare.Tabs.ProfileTab;
import picshare.mk.com.picshare.Tabs.SearchTab;
import picshare.mk.com.picshare.Tabs.ShowPictureOnMapTab;

public class MainActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTabs();
    }
    private void setTabs()
    {

        addTab("Home", R.drawable.home, new Intent().setClass(this, HomeTab.class));
        addTab("Search ", R.drawable.search, new Intent().setClass(this, SearchTab.class));
        addTab("Photo", R.drawable.cam, new Intent().setClass(this, PictureTab.class));
        addTab("Show", R.drawable.where, new Intent().setClass(this, ShowPictureOnMapTab.class));
        addTab("Profile",R.drawable.profile, new Intent().setClass(this, ProfileTab.class));


    }
    private void addTab(String labelId, int drawableId, Intent intent2)
    {
        TabHost tabHost = getTabHost();
        Intent intent = new Intent(intent2);
       TabHost.TabSpec spec = tabHost.newTabSpec(labelId);

        View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);
      //  TextView title = (TextView) tabIndicator.findViewById(R.id.title);
       // title.setText(labelId);
        ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
        icon.setImageResource(drawableId);

        spec.setIndicator(tabIndicator);
        spec.setContent(intent);
        tabHost.addTab(spec);
    }
}
