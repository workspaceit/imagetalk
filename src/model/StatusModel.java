package model;

import model.datamodel.Status;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mi on 8/21/15.
 */
public class StatusModel extends ImageTalkBaseModel {
    public int id;
    public String name = null;
    public String created_date = null;

    public StatusModel() {
        super();
        this.tableName = "status";
    }
    public ArrayList<Status> getAll(){
        ArrayList<Status> statusList = new ArrayList<Status>();
        String sql ="SELECT * FROM status";
        this.getData(sql);
        try {
            while(this.resultSet.next()){
                Status status=  new Status();

                status.id = this.resultSet.getInt("id");
                status.name = this.resultSet.getString("name");
                status.created_date = this.resultSet.getString("created_date");
                statusList.add(status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return statusList;
    }
    public Status getById(int id){
        Status status=  new Status();
        String sql ="SELECT * FROM status where id = "+id+" limit 1";
        this.getData(sql);
        try {
            while(this.resultSet.next()){
                status.id = this.resultSet.getInt("id");
                status.name = this.resultSet.getString("name");
                status.created_date = this.resultSet.getString("created_date");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return status;
    }

}
