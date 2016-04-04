package picshare.mk.com.picshare;

/**
 * Created by Malek on 4/4/2016.
 */
public class Post {
    private String title,userName,likes,imageUrl, userAvatar,location,date,id;
    public Post(String id,String title, String userName, String likes,String imageUrl, String userAvatar,String location,String date){
        this.title=title;
        this.userName=userName;
        this.id=id;
        this.likes=likes;
        this.imageUrl=imageUrl;
        this.userAvatar=userAvatar;
        this.location=location;
        this.date=date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvtar) {
        this.userAvatar = userAvtar;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }



}
