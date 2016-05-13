package model.datamodel.app;

/**
 * Application Name : ImageTalk
 * Package Name     : model.datamodel.app
 * Author           : Abu Bakar Siddique
 * Email            : absiddique.live@gmail.com
 * Created Date     : 5/11/16
 */
public class TagList {
    public int id;
    public AppCredential tagId;
    public int postId;
    public double originX;
    public double originY;
    public String tagMessage;
    public String createdDate;

    public TagList()
    {
        this.id=0;
        this.tagId = new AppCredential();
        this.postId = 0;
        this.originX = 0.0;
        this.originY = 0.0;
        this.tagMessage = "";
        this.createdDate = "";
    }
}
