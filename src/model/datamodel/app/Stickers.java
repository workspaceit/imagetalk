package model.datamodel.app;

/**
 * Created by mi on 10/13/15.
 */
public class Stickers {

    public int id;
    public int stickerCategoryId;
    public String categoryName;
    public String path;

    public Stickers() {
        this.id = 0;
        this.stickerCategoryId = 0;
        this.categoryName = "";
        this.path = "";
    }
}
