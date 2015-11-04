package model.datamodel.app.socket.chat;

import model.datamodel.app.AppCredential;
import model.datamodel.app.Contact;

/**
 * Created by mi on 10/29/15.
 */
public class ChatTransferStatus {
    public String chatId;
    public AppCredential appCredential;
    public boolean isRead;
    public boolean isOnline;

    public ChatTransferStatus() {
        this.chatId = "";
        this.appCredential = new AppCredential();
        this.isRead = false;
        this.isOnline = false;
    }
}
