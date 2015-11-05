package model.datamodel.app.video;

import java.util.ArrayList;

/**
 * Created by mi on 10/7/15.
 */
public class Videos {
    public VideoDetails original;
    public ArrayList<VideoDetails> others;


    public Videos() {
        this.original = new VideoDetails();
        this.others = new ArrayList<VideoDetails>();
    }
}