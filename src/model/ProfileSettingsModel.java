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
public class ProfileSettingsModel extends ImageTalkBaseModel {

    private int    id;
    private int user_id;
    private int notification;
    private String created_date;

    public ProfileSettingsModel() {
        super();
        super.tableName = "profile_settings";


        this.id = 0;
        this.user_id = 0;
        this.notification = 0;
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

    public int getNotification() {
        return notification;
    }

    public boolean setNotification(int notification){
        this.notification=notification;
        return true;
    }

    public int getNotificationStatus()
    {

        String query = "Select notification from "+this.tableName+" where user_id="+this.getCurrentUserId();

        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                this.notification = Integer.parseInt(this.resultSet.getString("notification")) ;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return notification;
    }

    public boolean isExist(){
        String query = "SELECT id FROM "+this.tableName+" where user_id = "+this.getCurrentUserId()+" limit 1";
        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return false;
    }

    public int insert(){

        String query = "INSERT INTO " + this.tableName + " (user_id,notification) VALUES ('" + this.getCurrentUserId() + "', '" + this.notification + "')";
        this.id = this.insertData(query);
        return  this.id;
    }

    public boolean updateNotificationStatus(){

        String query = "UPDATE " + this.tableName + " SET `notification` = " + this.notification
                +" where user_id="+this.getCurrentUserId();

        return this.updateData(query);
    }

}