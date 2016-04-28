package model;

import com.google.gson.Gson;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
    private String review;
    private String created_date;

    public UserReviewModel(){
        super();
        super.tableName = "user_review";


        this.id =0;
        this.from=0;
        this.to=0;
        this.review = "";
        this.created_date="";
    }

    public int getId() {
        return id;
    }

    public boolean setId(int id) {
        this.id = id;
        return true;
    }

    public int getFrom() {
        return from;
    }

    public boolean setFrom(int from){
        this.from=from;
        return true;
    }

    public int getTo() {
        return to;
    }

    public boolean setTo(int to){
        this.to=to;
        return true;
    }

    public String getReview() {
        return review;
    }

    public boolean setReview(String review){
        this.review=review;
        return true;
    }

    public int insert(){

          String query1 = "INSERT INTO "+this.tableName+" (`from`, `to`,`review`, `created_date`) " +
                        " VALUES (?,?,?,?)";

        PreparedStatement ps = null;
        try {
            ps = this.con.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
            ps.setInt((int) 1, this.from);
            ps.setInt((int) 2, this.to);
            ps.setString((int) 3, this.review);
            ps.setString((int)4, this.getUtcDateTime());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next())
            {
                this.id = rs.getInt(1);
            }
            rs.close();
            ps.close();
            this.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this.id;
    }




}
