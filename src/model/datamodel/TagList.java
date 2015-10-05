package model.datamodel;

import model.datamodel.app.AppCredential;

/**
 * Created by mi on 10/5/15.
 */
public class TagList {
    public int id;
    public AppCredential tagged;
    public int postId;
    public String createdDate;

    public TagList() {
        this.id = 0;
        this.tagged = new AppCredential();
        this.postId = 0;
        this.createdDate = "";
    }
}
