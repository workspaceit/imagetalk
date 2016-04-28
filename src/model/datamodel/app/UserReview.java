package model.datamodel.app;

/**
 * Application Name : ImageTalk
 * Package Name     : model.datamodel.app
 * Author           : Abu Bakar Siddique
 * Email            : absiddique.live@gmail.com
 * Created Date     : 4/27/16
 */
public class UserReview {

    public int    id;
    public User from;
    public User to;
    public String review;
    public String createdDate;

    public UserReview() {
        this.id = 0;
        this.from = new User();
        this.to = new User();
        this.review = "";
        this.createdDate = "";
    }
}
