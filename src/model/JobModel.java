package model;

import com.google.gson.Gson;
import model.datamodel.app.Job;
import model.datamodel.app.WallPost;
import model.datamodel.photo.Pictures;

import java.sql.SQLException;

/**
 * Created by rajib on 10/29/15.
 */
public class JobModel extends ImageTalkBaseModel {
    private int id;
    private String description;



    private String title;
    private String icon;
    private float price;
    private int payment_type;
    private int app_login_credential_id;
    private String created_date;

    private Gson gson;

    public JobModel() {
        this.tableName = "job";

        this.id = 0;
        this.description = "";
        this.icon="";
        this.price = 0;
        this.payment_type = 0;
        this.app_login_credential_id = 0;
        this.created_date = "";

        this.gson = new Gson();
    }

    public int getId() {
        return id;
    }

    public boolean setId(int id) {
        this.id = id;
        return true;
    }

    public String getDescription() {
        return description;
    }

    public boolean setDescription(String description) {
        this.description = description;
        return true;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public boolean setIcon(String icon) {
        this.icon = icon;
        return true;
    }

    public float getPrice() {
        return price;
    }

    public boolean setPrice(float price) {
        this.price = price;
        return  true;
    }

    public int getPayment_type() {
        return payment_type;
    }

    public boolean setPayment_type(int payment_type) {
        this.payment_type = payment_type;
        return true;
    }

    public int getApp_login_credential_id() {
        return app_login_credential_id;
    }

    public boolean setApp_login_credential_id(int app_login_credential_id) {
        this.app_login_credential_id = app_login_credential_id;
        return true;
    }

    public String getCreated_date() {
        return created_date;
    }

    public boolean setCreated_date(String created_date) {
        this.created_date = created_date;
        return true;
    }
    public boolean isExist(){
        String query = "SELECT * from "+this.tableName+" where app_login_credential_id = "+this.app_login_credential_id ;


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
    public Job getAllById(){

        String query = "SELECT * from "+this.tableName+" where id = "+this.id ;

        Job job = new Job();

        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                job.id = this.resultSet.getInt("id");
                job.appCredentialId = this.resultSet.getInt("app_login_credential_id");
                job.title = this.resultSet.getString("title");
                job.description = this.resultSet.getString("description");
                try{
                    job.icons = (this.resultSet.getObject("icon")==null || !this.resultSet.getString("icon").trim().startsWith("{"))?new Pictures():this.gson.fromJson(this.resultSet.getString("icon"),Pictures.class);

                }catch (Exception ex){
                    ex.printStackTrace();
                }
                job.price = this.resultSet.getFloat("price");
                job.paymentType = this.resultSet.getInt("payment_type");
                job.createdDate = this.getPrcessedTimeStamp(this.resultSet.getTimestamp("created_date"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return job;
    }

    public int insert(){
        String query = "INSERT INTO " + this.tableName + " (`app_login_credential_id`,`title`,`description`, `icon`, `price`,`payment_type`,`created_date` ) " +
                "VALUES ("+this.app_login_credential_id+",'"+this.title+"','"+this.description+"','"+this.icon+"',"+this.price+","+this.payment_type+",'"+this.getUtcDateTime()+"')";
        this.id = this.insertData(query);
        return this.id;
    }


}
