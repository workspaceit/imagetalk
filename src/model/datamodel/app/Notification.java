package model.datamodel.app;

/**
 * Created by mi on 6/9/16.
 */
public class Notification {
    public int id;
    public AppCredential person;
    public String actionTag;
    public String sourceClass;
    public Object source;
    public boolean isRead;
    public String createdDate;

    public Notification() {
        this.id = 0;
        this.person = new AppCredential();
        this.actionTag = "";
        this.sourceClass = "";
        this.source = new Object();
        this.isRead = false;
        this.createdDate = "";
    }
    /*public void setActionTagToLikePost(){
        this.actionTag = actionTagNames[0];
    }
    public void setActionTagToAddPost(){
        this.actionTag = actionTagNames[1];
    }
    public void setSourceTagToWallpost(){
        this.sourceClass = sourceTagNames[0];
    }
    public void setSourceTagToComment(){
        this.sourceClass = sourceTagNames[1];
    }*/
}