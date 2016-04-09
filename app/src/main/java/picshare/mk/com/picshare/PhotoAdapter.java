package picshare.mk.com.picshare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

import picshare.mk.com.picshare.Utils.DownloadImg;

/**
 * Created by mourad on 2016-04-04.
 */
public class PhotoAdapter extends ArrayAdapter<Photo> {
    public PhotoAdapter(Context context, List<Photo> Photo) {
        super(context, 0, Photo);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.photo_layout,parent, false);
        }
        PhotoViewHolder viewHolder = (PhotoViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new PhotoViewHolder();
            viewHolder.photo = (ImageView) convertView.findViewById(R.id.imageViewPhoto);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Buddies> buddies
        Photo photo = getItem(position);
        DownloadImg down = new DownloadImg();
        down.getImage((ImageView) convertView.findViewById(R.id.imageViewPhoto), photo.getPhoto());

        return convertView;
    }

    private class PhotoViewHolder{
        public ImageView photo;

    }
}
