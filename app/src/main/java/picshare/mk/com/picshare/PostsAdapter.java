package picshare.mk.com.picshare;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import picshare.mk.com.picshare.Utils.AppUtils;
import picshare.mk.com.picshare.Utils.DownloadImg;
import picshare.mk.com.picshare.Utils.MyGpsLocationListener;

/**
 * Created by Malek on 4/4/2016.
 */
public class PostsAdapter extends ArrayAdapter<Post> {
    MyGpsLocationListener gps;
    AppUtils appU;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    ImageView prof;

    public PostsAdapter(Context context, List<Post> Posts) {
        super(context, 0, Posts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int[] nbrLikes = new int[1];
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.posts_list_layout, parent, false);
        }
        appU = new AppUtils();
        TweetViewHolder viewHolder = (TweetViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new TweetViewHolder();
            viewHolder.userName = (TextView) convertView.findViewById(R.id.userName);
            viewHolder.likes = (TextView) convertView.findViewById(R.id.nbr_likes);
            viewHolder.postLocation = (TextView) convertView.findViewById(R.id.post_Location);
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.userPic);
            viewHolder.likesIcon = (ImageView) convertView.findViewById(R.id.likes);
            viewHolder.picture = (ImageView) convertView.findViewById(R.id.postPic);
            convertView.setTag(viewHolder);
        }
        gps = new MyGpsLocationListener(getContext());

        Post post = getItem(position);
        viewHolder.userName.setText(post.getUserName());
        viewHolder.likes.setText(post.getLikes() + " Likes");
        nbrLikes[0] = Integer.parseInt(post.getLikes());
        if (gps.canGetLocation()) {
            HashMap<String, Double> coord = appU.getCoordonatesFromString(post.getLocation());
            viewHolder.postLocation.setText(gps.getLocationName(coord.get("latitude"), coord.get("longitude")));
        } else {
            viewHolder.postLocation.setText("No Available location");
        }
        initImageLoader();
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.loading)        //	Display Stub Image
                .showImageForEmptyUri(R.drawable.loading)    //	If Empty image found
                .cacheInMemory()
                .cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();
        //Setting Images
        if (post.getImageUrl() != null && !post.getImageUrl().equalsIgnoreCase("null")
                && !post.getImageUrl().equalsIgnoreCase("")) {
            imageLoader.displayImage(post.getImageUrl(), viewHolder.picture, options,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri,
                                                      View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view,
                                    loadedImage);
                        }
                    });
        } else {
            viewHolder.picture.setImageResource(R.drawable.loading);
        }
        //
        DownloadImg down = new DownloadImg();
        prof = (ImageView) convertView.findViewById(R.id.userPic);
        new LoadImage().execute(post.getUserAvatar());

        final TweetViewHolder finalViewHolder = viewHolder;
        final TweetViewHolder finalViewHolder1 = viewHolder;
        viewHolder.likesIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  assert(R.id..heart_full == finalViewHolder.likesIcon.getDrawable());
                if (finalViewHolder.likesIcon.getDrawable().getConstantState() == getContext().getResources().getDrawable(R.drawable.heart_empty).getConstantState()) {
                    finalViewHolder.likesIcon.setImageResource(R.drawable.heart_full);
                    nbrLikes[0]++;
                    finalViewHolder1.likes.setText(nbrLikes[0] + " Likes");
                    //TODO Save the number of likes and the post id in localStorage to update the data base when the user closes the app.

                } else {
                    finalViewHolder.likesIcon.setImageResource(R.drawable.heart_empty);
                    nbrLikes[0]--;
                    finalViewHolder1.likes.setText(nbrLikes[0] + " Likes");
                    //TODO Save the number of likes and the post id in localStorage to update the data base when the user closes the app.

                }

            }
        });

        return convertView;
    }

    private class TweetViewHolder {
        public TextView userName;
        public TextView likes;
        public TextView postLocation;
        public ImageView avatar;
        public ImageView picture;
        public ImageView likesIcon;

    }

    private void initImageLoader() {
        int memoryCacheSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            int memClass = ((ActivityManager)
                    getContext().getSystemService(Context.ACTIVITY_SERVICE))
                    .getMemoryClass();
            memoryCacheSize = (memClass / 8) * 1024 * 1024;
        } else {
            memoryCacheSize = 2 * 1024 * 1024;
        }

        final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getContext()).threadPoolSize(5)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCacheSize(memoryCacheSize)
                .memoryCache(new FIFOLimitedMemoryCache(memoryCacheSize - 1000000))
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).enableLogging()
                .build();

        ImageLoader.getInstance().init(config);
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        Bitmap bitmap;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if (image != null) {
                prof.setImageBitmap(image);
            }
        }
    }
}
