package model.datamodel.app.socket.chat;

/**
 * Created by mi on 12/1/15.
 */
public class PrivateChatPhoto extends ChatPhoto {
    public int timer;
    public boolean tookSnapShot;

    public PrivateChatPhoto(int timer) {
        this.timer = 0;
        this.tookSnapShot = false;
    }
}
