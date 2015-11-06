package model.datamodel.app;

import java.util.ArrayList;

/**
 * Created by mi on 11/5/15.
 */
public class ChatHistory {
    Contact contact;
    public ArrayList<Chat> chat;

    public ChatHistory() {
        this.contact = new Contact();
        this.chat = new ArrayList<Chat>();
    }
}
