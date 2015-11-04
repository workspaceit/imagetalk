package model.datamodel.app.socket.chat;

import model.datamodel.app.AppCredential;

/**
 * Created by mi on 11/4/15.
 */
public class BaseChat {
    public int id;
    public String chatId;
    public AppCredential appCredential;
    public Object extra;
    public String createdDate;

    public BaseChat() {
        super();
        this.id = 0;
        this.chatId = "";
        this.appCredential = new AppCredential();
        this.createdDate = "";
    }
}
