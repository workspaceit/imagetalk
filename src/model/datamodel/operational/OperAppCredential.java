package model.datamodel.operational;

import model.datamodel.app.AppCredential;

/**
 * Created by mi on 10/2/15.
 */
public class OperAppCredential extends AppCredential {
    boolean banned;
    public OperAppCredential(){
        this.banned = false;
    }
}
