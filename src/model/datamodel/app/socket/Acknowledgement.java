package model.datamodel.app.socket;

import model.datamodel.app.AppCredential;

/**
 * Created by mi on 10/30/15.
 */
public class Acknowledgement{
    public int id;
    public String chatId;
    public String tmpChatId;
    public Object extra;
    public AppCredential appCredential;
    public boolean isRead;
    public boolean isOnline;
    public String lastSeen;
    public Acknowledgement() {
        this.id = 0;
        this.chatId = "";
        Object extra = new Object();
        this.appCredential = new AppCredential();
        this.isRead = false;
        this.isOnline = false;
        this.lastSeen = "";
    }
}