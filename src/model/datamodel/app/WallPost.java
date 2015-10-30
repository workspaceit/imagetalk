package model.datamodel.app;


import java.util.ArrayList;

/**
 * Created by mi on 10/5/15.
 */
public class WallPost {

    public int id;
    public  String description;
    public AppCredential owner;
    public  String picPath;
    public ArrayList<AppCredential> tagList;
    public ArrayList<Liker> likerList;
    public int tagCount;
    public int likeCount;
    public int commentCount;
    public boolean isLiked;
    public boolean isFavorite;
    public int type;
    public ArrayList<PostComment> comments;
    public Places places;
    public String createdDate;

   public WallPost() {
        this.id = 0;
        this.description = "";
        this.owner = new AppCredential();
        this.picPath = "";
        this.tagList = new ArrayList<AppCredential>();
        this.likerList = new ArrayList<Liker>();
        this.tagCount = 0;
        this.likeCount = 0;
        this.commentCount = 0;
        this.isLiked = false;
        this.isFavorite = false;
        this.type = 0;
        this.comments = new ArrayList<PostComment>();
        this.places = new Places();
        this.createdDate = "";
   }
}