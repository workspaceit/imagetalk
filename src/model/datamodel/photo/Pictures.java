package model.datamodel.photo;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by mi on 10/7/15.
 */
public class Pictures {
    public PictureDetails original;
    public ArrayList<PictureDetails> thumb;


    public Pictures() {
        this.original = new PictureDetails();
        this.thumb = new ArrayList<PictureDetails>();
    }
}