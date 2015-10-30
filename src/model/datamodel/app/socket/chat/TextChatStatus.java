package model.datamodel.app.socket.chat;

import model.datamodel.app.Contact;

/**
 * Created by mi on 10/29/15.
 */
public class TextChatStatus {
    public int id;
    public Contact contact;
    public String createdDate;

    public TextChatStatus() {
        this.id = 0;
        this.contact = new Contact();
        this.createdDate = "";
    }
}
