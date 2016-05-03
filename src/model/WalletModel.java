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
public class WalletModel extends ImageTalkBaseModel {

    private int    id;
    private double    total_credit;
    private int user_id;
    private String created_date;

    public WalletModel() {
        super();
        super.tableName = "wallet";


        this.id = 0;
        this.total_credit = 0.0;
        this.user_id = 0;
        this.created_date = "";

    }
    public int getId() {
        return id;
    }

    public boolean setId(int id) {
        this.id = id;
        return true;
    }

    public String getCreated_date() {
        return created_date;
    }

    public boolean setCreated_date(String created_date) {
        this.created_date = created_date;
        return true;
    }

    public int getUserId() {
        return user_id;
    }

    public boolean setUserId(int user_id){
        this.user_id=user_id;
        return true;
    }

    public double getTotalCredit() {
        return total_credit;
    }

    public boolean setTotalCredit(double total_credit){
        this.total_credit=total_credit;
        return true;
    }

    public double getTotalCreditByAppCredential(){

        String query = "Select total_credit from"+this.tableName+" where user_id="+this.getUserId();
        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                this.total_credit = Double.parseDouble(this.resultSet.getString("total_credit")) ;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return total_credit;

    }
}