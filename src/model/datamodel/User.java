package model.datamodel;

/**
 * Created by mi on 8/20/15.
 */
public class User {
    public int    id;
    public String f_name;
    public String l_name;
    public String address;
    public String created_date;

    public User() {
        this.id = 0;
        this.f_name = null;
        this.l_name = null;
        this.address = null;
        this.created_date = null;
    }
}
