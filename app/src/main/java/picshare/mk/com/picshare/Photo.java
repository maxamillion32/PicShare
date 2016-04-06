package picshare.mk.com.picshare;

/**
 * Created by mourad on 2016-04-04.
 */
public class Photo {
    private String photo;
    private String likes;
    private String title;


    public Photo(String photo) {
        //this.title = title;
        this.photo = photo;
        //this.likes = likes;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public String getPhoto() { return photo;   }

    /*public void setTitle(String title) {this.title = title;  }
    public String getTitle() {return title;  }

    public void setLikes(String likes) {
        this.likes = likes;
    }
    public String getLikes() { return likes;  }*/



}
