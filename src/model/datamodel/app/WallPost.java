package model.datamodel.app;


import java.util.ArrayList;

/**
 * Created by mi on 10/5/15.
 */
public class WallPost {

    public int id;
    public  String description;
    public String wallPostMood;
    public int commentCount;
    public AppCredential owner;
    public  String picPath;
    public ArrayList<TagList> tagList;
    public ArrayList<Liker> likerList;
    public int tagCount;
    public int likeCount;
    public boolean isLiked;
    public boolean isFavorite;
    public boolean isBlocked;
    public int type;
    public ArrayList<PostComment> comments;
    public Places places;
    public String createdDate;

   public WallPost() {
        this.id = 0;
        this.description = "";
        this.wallPostMood = "";
        this.commentCount = 0;
        this.owner = new AppCredential();
        this.picPath = "";
        this.tagList = new ArrayList<TagList>();
        this.likerList = new ArrayList<Liker>();
        this.tagCount = 0;
        this.likeCount = 0;
        this.commentCount = 0;
        this.isLiked = false;
        this.isFavorite = false;
        this.isBlocked = false;
        this.type = 0;
        this.comments = new ArrayList<PostComment>();
        this.places = new Places();
        this.createdDate = "";
   }

    @Override
    public String toString() {
        return "WallPost{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", wallPostMood='" + wallPostMood + '\'' +
                ", commentCount=" + commentCount +
                ", owner=" + owner +
                ", picPath='" + picPath + '\'' +
                ", tagList=" + tagList +
                ", likerList=" + likerList +
                ", tagCount=" + tagCount +
                ", likeCount=" + likeCount +
                ", isLiked=" + isLiked +
                ", isFavorite=" + isFavorite +
                ", type=" + type +
                ", comments=" + comments +
                ", places=" + places +
                ", createdDate='" + createdDate + '\'' +
                '}';
    }
}