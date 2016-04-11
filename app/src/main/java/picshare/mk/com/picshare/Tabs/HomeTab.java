package picshare.mk.com.picshare.Tabs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

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
import picshare.mk.com.picshare.Utils.AppUtils;
import picshare.mk.com.picshare.Utils.JSONParser;

public class HomeTab extends AppCompatActivity {
    List<Post> posts = new ArrayList<Post>();
    ListView mListView;
    AppUtils appUtils;
    private PostsTasks postTask=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_tab);
        mListView = (ListView)findViewById(R.id.PostsListView);
         appUtils= new AppUtils();
        if(this.isNetworkAvailable()){
            postTask= new PostsTasks();
            postTask.execute();
        }else{
            appUtils.noInternetConnection(this.isNetworkAvailable(), HomeTab.this);
        }


    }
    public class PostsTasks extends AsyncTask<String, String, List<Post>> {

        protected List<Post> doInBackground(String... params) {
            if(isNetworkAvailable()) {
                List<Post> posts = new ArrayList<Post>();

                ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();


                JSONParser jParser = new JSONParser();
                JSONObject json = jParser.makeHttpRequest("http://picshare-android.esy.es/ws/getPosts.php", "GET", param);

                int success = 0;
                try {
                    success = json.getInt("success");
                    if (success == 1) {
                        JSONArray postsData = json.getJSONArray("Posts");
                        for (int i = 0; i < postsData.length(); i++) {
                            JSONObject postData = postsData.getJSONObject(i);
                            String userName = postData.getString("firstName") + " " + postData.getString("lastName");
                            String userAvatar = postData.getString("avatar_url");
                            String postId = postData.getString("post_id");
                            String title = postData.getString("title");
                            String picture = postData.getString("image_url");
                            String date = postData.getString("date");
                            String likes = postData.getString("likes");
                            String location = postData.getString("location");
                            posts.add(new Post(postId, title, userName, likes, picture, userAvatar, location, date));
                            System.out.println(posts.get(i));
                        }
                        return posts;
                    } else {
                        System.out.println("Failure");
                        posts.add(new Post("", "No Posts", "", "", "", "", "", ""));
                        return posts;
                    }

                } catch (JSONException e) {
                    posts.add(new Post("", "No Posts", "", "", "", "", "", ""));
                    return posts;
                }
            }else{

                return null;
            }
        }
        protected void onPostExecute(List<Post> posts) {
            super.onPostExecute(posts);
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeTab.this);

            if(posts==null){//No Internet connection
                alertDialogBuilder.setMessage("Sorry There is no Internet Connection !! ");
                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }else{
                if(posts.get(0).getTitle()=="No Posts"){ //No Posts
                    alertDialogBuilder.setMessage("Sorry There are no Posts !! ");
                    alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                          //  finish();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }else{
                    PostsAdapter adapter = new PostsAdapter(HomeTab.this, posts);
                    mListView.setAdapter(adapter);
                }
            }
     }
    }
    @Override
    public void onResume(){
        super.onResume();
        if(isNetworkAvailable()) {
            postTask = new PostsTasks();
            postTask.execute();
        }else{
            Toast.makeText(HomeTab.this, "No Internet Connection !!", Toast.LENGTH_LONG).show();
        }

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
