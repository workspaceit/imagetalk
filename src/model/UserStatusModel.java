package model;

import model.datamodel.Status;
import model.datamodel.UserStatus;

import java.sql.SQLException;

/**
 * Created by mi on 8/21/15.
 */
public class UserStatusModel extends ImageTalkBaseModel {
    public int id;
    public int status_id;
    public int login_id;
    public String created_date;

    public UserStatusModel() {
        this.id = 0;
        this.status_id = 0;
        this.login_id = 0;
        this.created_date = null;
    }

    public int insert(){
        String sql ="INSERT INTO `user_status`(`status_id`, `login_id`) VALUES ("+this.status_id+","+this.login_id+")";
        return this.insertData(sql);
    }
    public Status getByLoginId(int login_id){
        String sql ="SELECT * FROM `user_status` WHERE login_id = "+login_id;
        this.getData(sql);
        UserStatus userStatus = new UserStatus();
        Status status= new Status();
        try {
            while(this.resultSet.next()){
                userStatus.status_id =  this.resultSet.getInt("status_id");
                userStatus.login_id = this.resultSet.getInt("login_id");
                userStatus.created_date = this.resultSet.getString("created_date");
            }
            StatusModel statusModel =  new StatusModel();
            status = statusModel.getById(userStatus.status_id );
        } catch (SQLException e) {
            e.printStackTrace();

        }finally {
            this.closeConnection();
        }
        return status;
    }
}