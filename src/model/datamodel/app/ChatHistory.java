package model.datamodel.app;

import java.util.ArrayList;

/**
 * Created by mi on 11/5/15.
 */
public class ChatHistory {
    public Contact contact;
    public ArrayList<Chat> chat;
    public int unRead;

    public ChatHistory() {
        this.contact = new Contact();
        this.chat = new ArrayList<Chat>();
        this.unRead = 0;
    }
}
