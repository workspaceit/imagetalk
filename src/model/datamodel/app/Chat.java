package model.datamodel.app;


/**
 * Created by mi on 11/3/15.
 */
public class Chat {
    public long id;
    public String chatId;
    public int to;
    public int from;
    public String chatText;
    public Object extra;
    public Object mediaPath;
    public int type;
    public String createdDate;
    public boolean readStatus;

    public Chat() {
        this.id = 0;
        this.chatId = "";
        this.to = 0;
        this.from = 0;
        this.chatText = "";
        this.extra = new Object();
        this.mediaPath = new Object();
        this.type = 0;
        this.createdDate = "";
        this.readStatus = false;
    }
}