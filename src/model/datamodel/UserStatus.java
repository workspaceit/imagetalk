package model.datamodel;

/**
 * Created by mi on 8/21/15.
 */
public class UserStatus {
    public int id;
    public int status_id;
    public int login_id;
    public String created_date;

    public UserStatus(){
        this.id= 0;
        this.status_id = 0;
        this.login_id = 0;
        this.created_date =null;
    }
}
