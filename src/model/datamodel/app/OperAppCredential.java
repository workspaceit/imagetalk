package model.datamodel.app;

/**
 * Application Name : ImageTalt
 * Package Name     : model.datamodel.app
 * Author           : Abu Bakar Siddique
 * Email            : absiddique.live@gmail.com
 * Created Date     : 10/2/15
 */
public class OperAppCredential extends AppCredential {
    public boolean banned;
    public boolean active;

    public OperAppCredential() {
        this.active = true;
        this.banned = false;
    }
}
