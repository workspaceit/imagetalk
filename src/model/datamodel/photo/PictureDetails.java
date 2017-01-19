package model.datamodel.photo;

/**
 * Created by mi on 10/7/15.
 */
public class PictureDetails {

    public String path;
    public String type;
    public PictureSize size;

    public PictureDetails() {
        this.path = "";
        this.type = "";
        this.size = new PictureSize();
    }

    @Override
    public String toString() {
        return "PictureDetails{" +
                "path='" + path + '\'' +
                ", type='" + type + '\'' +
                ", size=" + size +
                '}';
    }
}
