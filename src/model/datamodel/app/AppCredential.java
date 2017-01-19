package model.datamodel.app;

/**
 * Created by mi on 10/1/15.
 */
public class AppCredential {
    public int id;
    public String textStatus;
    public String phoneNumber;
    public User user;
    public Job job;
    public String createdDate;
    public AppCredential(){
        this.id = 0;
        this.textStatus = "";
        this.phoneNumber = "";
        this.user = new User();
        this.job = new Job();
        this.createdDate = "";
    }

    @Override
    public String toString() {
        return "AppCredential{" +
                "id=" + id +
                ", textStatus='" + textStatus + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", user=" + user +
                ", job=" + job +
                ", createdDate='" + createdDate + '\'' +
                '}';
    }
}
