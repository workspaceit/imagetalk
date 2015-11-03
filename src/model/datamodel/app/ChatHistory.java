package model.datamodel.app;


/**
 * Created by mi on 11/3/15.
 */
public class ChatHistory {
    private long id;
    private long chatId;
    private int to;
    private int from;
    private String chatText;
    private Object extra;
    private Object mediaPath;
    private int type;
    private String createdDate;
    private boolean readStatus;

    public ChatHistory() {
        this.id = 0;
        this.chatId = 0;
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