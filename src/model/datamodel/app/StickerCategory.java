package model.datamodel.app;

import java.util.ArrayList;

/**
 * Created by mi on 10/13/15.
 */
public class StickerCategory {
    public int    id;
    public String name;
    public ArrayList<Stickers> stickers;

    public StickerCategory() {
        this.id = 0;
        this.name = "";
        this.stickers = new ArrayList();
    }
}
