package model.datamodel.admin;

import model.datamodel.app.User;

/**
 * Created by mi on 11/4/16.
 */
public class AdminCredential  {
    public int id;
    public String email;
    public User user;

    public AdminCredential() {
        this.id = 0;
        this.email = "";
        this.user = new User();
    }
}
