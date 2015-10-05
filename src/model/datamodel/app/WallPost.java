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
    public ArrayList<AppCredential> taglist;
    public int tagCount;
    public int likeCount;
    public int commentCount;
    public ArrayList<PostComment> comments;
    public Location location;
    public String createdDate;

   public WallPost() {
     this.id = 0;
     this.description = "";
     this.owner = new AppCredential();
     this.picPath = "";
     this.taglist = new ArrayList<AppCredential>();
     this.tagCount = 0;
     this.likeCount = 0;
     this.commentCount = 0;
     this.comments = new ArrayList<PostComment>();
     this.location = new Location();
     this.createdDate = "";
   }
}
