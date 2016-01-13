package model.datamodel.app.socket.chat;

import model.datamodel.app.video.VideoDetails;
import model.datamodel.app.video.Videos;
import model.datamodel.photo.Pictures;

/**
 * Created by mi on 1/13/16.
 */
public class ChatVideo extends BaseChat{

    public String caption;
    public VideoDetails video;

    public ChatVideo(){
        super();
        this.type = 7;
        this.caption = "";

        this.video = new VideoDetails();
    }
}
