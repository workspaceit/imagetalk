package model.datamodel.app;

import java.util.ArrayList;

/**
 * Created by mi on 10/13/15.
 */
public class StickerCategory {
    public int    id;
    public String name;
    public String coverPicPath;
    public ArrayList<Stickers> stickers;

    public StickerCategory() {
        this.id = 0;
        this.name = "";
        this.coverPicPath = "";
        this.stickers = new ArrayList();
    }
}
