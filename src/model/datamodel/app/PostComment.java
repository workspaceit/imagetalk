package model.datamodel.app;


import java.util.ArrayList;

/**
 * Created by mi on 10/5/15.
 */
public class PostComment {

    public int           id;
    public String        comment;
    public int           postId;
    public AppCredential commenter;
    public String        picPath;
    public ArrayList<PostCommentReply> commentReplies;
    public String createdDate;

    public PostComment() {
        this.id = 0;
        this.comment = "";
        this.postId = 0;
        this.commenter = new AppCredential();
        this.picPath = "";
        this.commentReplies = new ArrayList<PostCommentReply>();
        this.createdDate = "";
    }
}

