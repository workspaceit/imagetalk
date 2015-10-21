package model.datamodel.app;

/**
 * Created by mi on 10/19/15.
 */
public class Contact extends AppCredential{
    public int contactId;
    public String nickName;
    public boolean favorites;
    public boolean isBlock;
    public int rating;
    public String contactCreatedDate;

    public Contact() {
        this.contactId = 0;
        this.nickName = "";
        this.favorites = false;
        this.isBlock = false;
        this.rating = 0;
        this.contactCreatedDate = "";
    }
}
