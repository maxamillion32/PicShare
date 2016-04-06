package picshare.mk.com.picshare.Tabs;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import picshare.mk.com.picshare.Photo;
import picshare.mk.com.picshare.PhotoAdapter;
import picshare.mk.com.picshare.Post;
import picshare.mk.com.picshare.R;
import picshare.mk.com.picshare.Utils.JSONParser;

public class ProfileTab extends AppCompatActivity {

    List<Photo> photos = new ArrayList<Photo>();
    GridView gridViewPhoto ;
    TextView pseudo;
    TextView nbLikes;
    TextView nbPublications;
    private PhotoTasks mListTask = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_tab);
        pseudo = (TextView) findViewById(R.id.pseudo);
        pseudo.setText("Mourad Mamlouk");
        gridViewPhoto = (GridView) findViewById(R.id.gridViewPhoto);
        nbLikes = (TextView) findViewById(R.id.nbL);
        nbPublications = (TextView) findViewById(R.id.nbP);
        mListTask = new PhotoTasks();
        mListTask.execute();
    }

    public class PhotoTasks extends AsyncTask<String, String, List<Photo>> {
        //private List<Buddies> genererBuddies() {
        int posts=0;
        int l=0;
        protected List<Photo> doInBackground(String... params) {
            List<Photo> photoList = new ArrayList<Photo>();
            ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("email", "elkamel_malek@hotmail.fr"));
            JSONParser jParser = new JSONParser();
            JSONObject json = jParser.makeHttpRequest("http://picshare-android.esy.es/ws/getPhotos.php", "GET", param);
            try {
                int success = json.getInt("success");
                if (success == 1) {
                    JSONArray postsData = json.getJSONArray("Photos");
                    posts=postsData.length();
                    for (int i = 0; i < posts; i++) {
                        JSONObject postData = postsData.getJSONObject(i);
                        String image_url = postData.getString("image_url");
                        String likes=postData.getString("likes");
                        l+=Integer.parseInt(likes);
                        String title=postData.getString("title");
                        System.out.println(image_url + " " + likes + " " + title);
                        photoList.add(new Photo(image_url));
                        System.out.println(photoList.get(i));
                    }

                    return photoList;
                } else {
                    System.out.println("Failure");
                    return photoList;
                }

            } catch (JSONException e) {
                return photoList;
            }

        }
        protected void onPostExecute(List<Photo> photos) {
            PhotoAdapter adapter = new PhotoAdapter(ProfileTab.this, photos);
            gridViewPhoto.setAdapter(adapter);
            nbLikes.setText(l+"");
            System.out.println("nombre de posts: "+ posts);
            nbPublications.setText(posts+"");
            super.onPostExecute(photos);
        }
    }
}
