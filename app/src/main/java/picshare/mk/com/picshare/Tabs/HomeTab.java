package picshare.mk.com.picshare.Tabs;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import picshare.mk.com.picshare.Post;
import picshare.mk.com.picshare.PostsAdapter;
import picshare.mk.com.picshare.R;
import picshare.mk.com.picshare.Utils.JSONParser;

public class HomeTab extends AppCompatActivity {
    List<Post> posts = new ArrayList<Post>();
    ListView mListView;
    private PostsTasks postTask=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_tab);
        mListView = (ListView)findViewById(R.id.PostsListView);
        postTask= new PostsTasks();
        postTask.execute();
    }
    public class PostsTasks extends AsyncTask<String, String, List<Post>> {

        protected List<Post> doInBackground(String... params) {
            List<Post> posts = new ArrayList<Post>();

            ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();


            JSONParser jParser = new JSONParser();
            JSONObject json = jParser.makeHttpRequest("http://picshare-android.esy.es/ws/getPosts.php", "GET", param);
        System.out.println(json);
            try {
                int success = json.getInt("success");
                if (success == 1) {
                    JSONArray postsData = json.getJSONArray("Posts");
                    for (int i = 0; i < postsData.length(); i++) {
                        JSONObject postData = postsData.getJSONObject(i);
                        String userName = postData.getString("firstName")+" "+postData.getString("lastName");
                        String userAvatar = postData.getString("avatar_url");
                        String postId = postData.getString("post_id");
                        String title=postData.getString("title");
                        String picture = postData.getString("image_url");
                        String date = postData.getString("date");
                        String likes=postData.getString("likes");
                        String location=postData.getString("location");
                        posts.add(new Post(postId,title, userName, likes, picture,userAvatar,location,date));
                        System.out.println(posts.get(i));
                    }
                    return posts;
                } else {
                    System.out.println("Failure");
                    posts.add(new Post("","No Posts", "", "", "","","",""));
                    return posts;
                }

            } catch (JSONException e) {
                posts.add(new Post("","No Posts", "", "", "","","",""));
                return posts;
            }

        }
        protected void onPostExecute(List<Post> posts) {
            PostsAdapter adapter = new PostsAdapter(HomeTab.this, posts);
            super.onPostExecute(posts);
            mListView.setAdapter(adapter);



        }
    }
}
