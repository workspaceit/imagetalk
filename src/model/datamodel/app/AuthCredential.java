package model.datamodel.app;

/**
 * Created by mi on 10/1/15.
 */
public class AuthCredential extends AppCredential {
    public String accessToken;
    public AuthCredential(){
        this.accessToken = "";
    }
}
