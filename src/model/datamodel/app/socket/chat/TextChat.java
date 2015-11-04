package model.datamodel.app.socket.chat;

import model.datamodel.app.AppCredential;
import model.datamodel.app.Contact;

/**
 * Created by mi on 10/29/15.
 */
public class TextChat extends BaseChat {

    public String text;

    public TextChat() {
        super();
        this.text = "";
    }
}
