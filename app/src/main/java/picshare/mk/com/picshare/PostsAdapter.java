package picshare.mk.com.picshare;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import picshare.mk.com.picshare.Utils.DownloadImg;

/**
 * Created by Malek on 4/4/2016.
 */
public class PostsAdapter extends ArrayAdapter<Post> {

    public PostsAdapter(Context context, List<Post> Posts) {
        super(context, 0, Posts);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int[] nbrLikes = new int[1];
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.posts_list_layout,parent, false);
        }

        TweetViewHolder viewHolder = (TweetViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new TweetViewHolder();
            viewHolder.userName = (TextView) convertView.findViewById(R.id.userName);
            viewHolder.likes = (TextView) convertView.findViewById(R.id.nbr_likes);
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.userPic);
            viewHolder.likesIcon = (ImageView) convertView.findViewById(R.id.likes);
            viewHolder.picture = (ImageView) convertView.findViewById(R.id.postPic);
            convertView.setTag(viewHolder);
        }


        Post post = getItem(position);
        viewHolder.userName.setText(post.getUserName());
        viewHolder.likes.setText(post.getLikes()+" Likes");
        nbrLikes[0] =Integer.parseInt(post.getLikes());

        DownloadImg down = new DownloadImg();
        down.getImage((ImageView) convertView.findViewById(R.id.userPic), post.getUserAvatar());
        down.getImage((ImageView) convertView.findViewById(R.id.postPic), post.getImageUrl());
        final TweetViewHolder finalViewHolder = viewHolder;
        final TweetViewHolder finalViewHolder1 = viewHolder;
        viewHolder.likesIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  assert(R.id..heart_full == finalViewHolder.likesIcon.getDrawable());
                if(finalViewHolder.likesIcon.getDrawable().getConstantState()== getContext().getResources().getDrawable(R.drawable.heart_empty).getConstantState() )
                {
                    finalViewHolder.likesIcon.setImageResource(R.drawable.heart_full);
                    nbrLikes[0]++;
                    finalViewHolder1.likes.setText(nbrLikes[0]+" Likes");
                    //TODO Save the number of likes and the post id in localStorage to update the data base when the user closes the app.

                }else{
                    finalViewHolder.likesIcon.setImageResource(R.drawable.heart_empty);
                    nbrLikes[0]--;
                    finalViewHolder1.likes.setText(nbrLikes[0]+" Likes");
                    //TODO Save the number of likes and the post id in localStorage to update the data base when the user closes the app.

                }

            }
        });

        return convertView;
    }
    private class TweetViewHolder{
        public TextView userName;
        public TextView likes;
        public ImageView avatar;
        public ImageView picture;
        public ImageView likesIcon;

    }
}
