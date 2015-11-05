package model.datamodel.app;

import java.util.ArrayList;

/**
 * Created by mi on 11/5/15.
 */
public class ChatHistory {
    AppCredential appCredential;
    ArrayList<ChatHistory> chatHistories;

    public ChatHistory() {
        this.appCredential = new AppCredential();
        this.chatHistories = new ArrayList<ChatHistory>();
    }
}
