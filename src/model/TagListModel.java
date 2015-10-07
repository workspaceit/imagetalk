package model;

import model.datamodel.TagList;
import model.datamodel.app.AppCredential;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mi on 10/5/15.
 */
public class TagListModel extends ImageTalkBaseModel{
    private int id;
    private int tag_id;
    private int post_id;
    private String created_date;

    public TagListModel(){
        super();
        super.tableName = "tag_list";

        this.id = 0;
        this.tag_id = 0;
        this.post_id = 0;
        this.created_date = "";
    }

    public int getId() {
        return id;
    }

    public boolean setId(int id) {
        this.id = id;
        return true;
    }

    public int getTag_id() {
        return tag_id;
    }

    public boolean setTag_id(int tag_id) {
        this.tag_id = tag_id;
        return true;
    }

    public int getPost_id() {
        return post_id;
    }

    public boolean setPost_id(int post_id) {
        this.post_id = post_id;
        return true;
    }

    public String getCreated_date() {
        return created_date;
    }

    public boolean setCreated_date(String created_date) {
        this.created_date = created_date;
        return true;
    }
    public ArrayList<AppCredential> getByPostId(){
        ArrayList<AppCredential> tagList  = new ArrayList<AppCredential>();

        String query = "SELECT  " +
                " app_login_credential.id as app_login_credentialId, app_login_credential.text_status, app_login_credential.phone_number, app_login_credential.created_date as app_lCdate," +
                " user_inf.id as user_infId, user_inf.f_name, user_inf.l_name, user_inf.pic_path, user_inf.address_id, user_inf.created_date as user_infCdate," +
                " location.id as locationId, location.lat, location.lng, location.formatted_address, location.country, location.created_date as locationCDate" +
                " FROM  " +this.tableName+
                " join app_login_credential on app_login_credential.id = " +this.tableName+".tag_id"+
                " join user_inf on user_inf.id = app_login_credential.u_id " +
                " left join location on location.id = user_inf.address_id " +
                " where "+this.tableName+".post_id= "+this.post_id;

        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                AppCredential appCredential = new AppCredential();

                appCredential.id = this.resultSet.getInt("app_login_credentialId");
                appCredential.textStatus = this.resultSet.getString("text_status");
                appCredential.phoneNumber = this.resultSet.getString("phone_number");
                appCredential.createdDate = this.resultSet.getString("app_lCdate");

                appCredential.user.id = this.resultSet.getInt("user_infId");
                appCredential.user.firstName = this.resultSet.getString("f_name");
                appCredential.user.lastName = this.resultSet.getString("l_name");
                appCredential.user.picPath = this.resultSet.getString("pic_path");


                appCredential.user.address.id = (this.resultSet.getObject("locationId")==null)?0:this.resultSet.getInt("locationId");
                appCredential.user.address.lat = (this.resultSet.getObject("lat")==null)?0:this.resultSet.getDouble("lat");
                appCredential.user.address.lng = (this.resultSet.getObject("lng")==null)?0:this.resultSet.getDouble("lng");
                appCredential.user.address.formattedAddress = (this.resultSet.getObject("formatted_address")==null)?"":this.resultSet.getString("formatted_address");
                appCredential.user.address.countryName = (this.resultSet.getObject("country")==null)?"":this.resultSet.getString("country");
                appCredential.user.address.createdDate = (this.resultSet.getObject("locationCDate")==null)?"":this.resultSet.getString("locationCDate");

                tagList.add(appCredential);


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return tagList;
    }
    public int insert(){
        String query = "INSERT INTO tag_list(tag_id, post_id) VALUES ("+this.tag_id+","+this.post_id+")";
        this.id = this.insertData(query);
        return this.id;
    }
}
