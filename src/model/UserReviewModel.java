package model;

import com.google.gson.Gson;

/**
 * Application Name : ImageTalk
 * Package Name     : model
 * Author           : Abu Bakar Siddique
 * Email            : absiddique.live@gmail.com
 * Created Date     : 4/27/16
 */
public class UserReviewModel extends ImageTalkBaseModel{

    private int id;
    private int from;
    private int to;
    private String created_date;

    public UserReviewModel(){
        super();
        super.tableName = "user_review";


        this.id =0;
        this.from=0;
        this.to=0;
        this.created_date="";
    }

    public int getId() {
        return id;
    }

    public boolean setId(int id) {
        this.id = id;
        return true;
    }

    public int getfrom() {
        return from;
    }

    public boolean setFrom(int from){
        this.from=from;
        return true;
    }

    public int getto() {
        return to;
    }

    public boolean setTo(int to){
        this.to=to;
        return true;
    }




}
