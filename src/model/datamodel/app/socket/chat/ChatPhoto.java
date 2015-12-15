package model.datamodel.app.socket.chat;

import model.datamodel.app.AppCredential;
import model.datamodel.photo.Pictures;

/**
 * Created by mi on 11/4/15.
 */
public class ChatPhoto extends BaseChat{


    public String caption;
    public String base64Img;
    public Pictures pictures;

    public ChatPhoto(){
        super();
        this.caption = "";
        this.base64Img = "";
        this.type = 1;
    }
}
