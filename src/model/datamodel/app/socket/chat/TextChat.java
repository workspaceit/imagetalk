package model.datamodel.app.socket.chat;

import model.datamodel.app.AppCredential;
import model.datamodel.app.Contact;

/**
 * Created by mi on 10/29/15.
 */
public class TextChat {
    public int id;
    public String chatId;
    public AppCredential appCredential;
    public String text;
    public Object extra;
    public String createdDate;

    public TextChat() {
        this.id = 0;
        this.chatId = "";
        this.appCredential = new AppCredential();
        this.text = "";
        this.extra = new Object();
        this.createdDate = "";
    }
}
