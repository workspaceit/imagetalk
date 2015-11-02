package model.datamodel.app.socket.chat;

import model.datamodel.app.Contact;

/**
 * Created by mi on 10/29/15.
 */
public class TextChatStatus {
    public int chatId;
    public int appCredentialId;
    public boolean isRead;
    public boolean isOnline;

    public TextChatStatus() {
        this.chatId = 0;
        this.appCredentialId = 0;
        this.isRead = false;
        this.isOnline = false;
    }
}
