package model.datamodel.app.socket;

/**
 * Created by mi on 10/30/15.
 */
public class Acknowledgement {
    public int chatId;
    public int appCredentialId;
    public boolean isRead;

    public Acknowledgement() {
        this.chatId = 0;
        this.appCredentialId = 0;
        this.isRead = false;
    }
}