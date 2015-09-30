package model.datamodel;

/**
 * Created by mi on 8/20/15.
 */
public class User {
    public int    id;
    public String firstName;
    public String lastName;
    public String picPath;
    public Location address;
    public String createdDate;

    public User() {
        this.id = 0;
        this.firstName = null;
        this.lastName = null;
        this.address = new Location();
        this.picPath = "";
        this.createdDate = "";
    }
}
