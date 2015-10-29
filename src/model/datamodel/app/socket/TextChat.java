package model.datamodel.app.socket;

/**
 * Created by mi on 10/29/15.
 */
public class TextChat {
    public int id;
    public int appCredentialId;
    public String text;
    public Object extra;
    public boolean isRead;
    public String createdDate;

    public TextChat() {
        this.id = 0;
        this.appCredentialId = 0;
        this.text = "";
        this.extra = new Object();
        this.isRead = false;
        this.createdDate = "";
    }
}
