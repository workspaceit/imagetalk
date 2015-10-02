package model.datamodel.app;

/**
 * Created by mi on 8/21/15.
 */
public class Login {
    public int id;
    public String email;
    public String access_token;
    public String created_date;
    public int active;
    public int u_id;
    public int type;
    public String activation_code;
    public User user;

    public Login(){
        this.id=0;
        this.email = "";
        this.access_token = "";
        this.created_date = "";
        this.active = 0;
        this.u_id = 0;
        this.type = 0;
        this.activation_code = "";
        this.user = new User();
    }
}