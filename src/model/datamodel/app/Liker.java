package model.datamodel.app;

/**
 * Created by mi on 10/8/15.
 */
public class Liker extends AppCredential{
    public int likeId;
    public String likedDate;

    public Liker() {
        this.likeId = 0;
        this.likedDate = "";
    }
}
