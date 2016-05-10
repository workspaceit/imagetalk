package model;


import com.google.gson.Gson;
import model.datamodel.app.AppCredential;
import model.datamodel.photo.Pictures;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mi on 10/5/15.
 */
public class TagListModel extends ImageTalkBaseModel{
    private int id;
    private int tag_id;
    private int post_id;
    private double origin_x;
    private double origin_y;
    private String tag_message;
    private String created_date;
    private Gson gson;

    public TagListModel(){
        super();
        super.tableName = "tag_list";

        this.id = 0;
        this.tag_id = 0;
        this.post_id = 0;
        this.origin_x = 0.0;
        this.origin_y = 0.0;
        this.tag_message = "";
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

    public int getTag_id() {
        return tag_id;
    }

    public boolean setTag_id(int tag_id) {
        this.tag_id = tag_id;
        return true;
    }

    public double getOriginX() {
        return origin_x;
    }

    public boolean setOriginX(double origin_x) {
        this.origin_x = origin_x;
        return true;
    }

    public double getOriginY() {
        return origin_y;
    }

    public boolean setOriginY(double origin_y) {
        this.origin_y = origin_y;
        return true;
    }

    public String getTagMessage() {
        return tag_message;
    }

    public boolean setTagMessage(String tag_message) {
        this.tag_message = tag_message;
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
                " location.id as locationId, location.lat, location.lng, location.formatted_address, location.country, location.created_date as locationCDate,job.*" +
                " FROM  " +this.tableName+
                " join app_login_credential on app_login_credential.id = " +this.tableName+".tag_id"+
                " join user_inf on user_inf.id = app_login_credential.u_id " +
                " left join location on location.id = user_inf.address_id " +
                " left join job on job.app_login_credential_id = app_login_credential.id " +
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

                try{
                    appCredential.user.picPath = this.gson.fromJson(this.resultSet.getString("pic_path"), Pictures.class);
                }catch (Exception ex){
                    appCredential.user.picPath.original.path = this.resultSet.getString("pic_path");
                    System.out.println("Parse error on picture appCid "+ appCredential.id);
                    ex.printStackTrace();
                }

                appCredential.user.address.id = (this.resultSet.getObject("locationId")==null)?0:this.resultSet.getInt("locationId");
                appCredential.user.address.lat = (this.resultSet.getObject("lat")==null)?0:this.resultSet.getDouble("lat");
                appCredential.user.address.lng = (this.resultSet.getObject("lng")==null)?0:this.resultSet.getDouble("lng");
                appCredential.user.address.formattedAddress = (this.resultSet.getObject("formatted_address")==null)?"":this.resultSet.getString("formatted_address");
                appCredential.user.address.countryName = (this.resultSet.getObject("country")==null)?"":this.resultSet.getString("country");
                appCredential.user.address.createdDate = (this.resultSet.getObject("locationCDate")==null)?"":this.resultSet.getString("locationCDate");

                //job details
                appCredential.job.id =(this.resultSet.getObject("job.id")==null)?0:this.resultSet.getInt("job.id");
                appCredential.job.appCredentialId = (this.resultSet.getObject("job.app_login_credential_id")==null)?0:this.resultSet.getInt("job.app_login_credential_id");
                appCredential.job.title = (this.resultSet.getObject("job.title") == null) ? "" : this.resultSet.getString("job.title");
                appCredential.job.description = (this.resultSet.getObject("job.description") == null)? "" : this.resultSet.getString("job.description");
                try{
                    appCredential.job.icons = (this.resultSet.getObject("job.icon")==null || !this.resultSet.getString("job.icon").trim().startsWith("{"))?new Pictures():this.gson.fromJson(this.resultSet.getString("job.icon"),Pictures.class);

                }catch (Exception ex){
                    ex.printStackTrace();
                }
                appCredential.job.price = (this.resultSet.getObject("job.price") == null)?0:this.resultSet.getFloat("job.price");
                appCredential.job.paymentType = (this.resultSet.getObject("job.payment_type") == null)?0:this.resultSet.getInt("job.payment_type");
                try {
                    appCredential.job.createdDate = (this.resultSet.getObject("job.created_date") == null)?"":this.getPrcessedTimeStamp(this.resultSet.getTimestamp("job.created_date"));
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                    appCredential.job.createdDate = "";
                }
                //end job details


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
        String query = "INSERT INTO tag_list( tag_id, post_id, origin_x, origin_y, tag_message ) "
                +"VALUES ("+this.tag_id+","+this.post_id+","+this.origin_x+","+this.origin_y+",'"+this.tag_message+"')";
        System.out.println(query);
        this.id = this.insertData(query);
        return this.id;
    }
}
