package model;

import com.google.gson.Gson;
import model.datamodel.app.AppCredential;
import model.datamodel.app.Liker;
import model.datamodel.app.WallPost;
import model.datamodel.photo.Pictures;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mi on 10/7/15.
 */
public class PostLikeModel extends  ImageTalkBaseModel {

    private int id;
    private int liker_id;
    private int post_id;
    private String created_date;

    private Gson gson = new Gson();
    public PostLikeModel() {
        super();
        super.tableName = "post_like";
        this.gson = new Gson();
    }

    public int getId() {
        return id;
    }

    public boolean setId(int id) {
        this.id = id;
        return true;
    }

    public int getLiker_id() {
        return liker_id;
    }

    public boolean setLiker_id(int liker_id) {
        this.liker_id = liker_id;
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

    public int insert(){
        if(this.isAlreadyLiked()){
            return  0;
        }
        String query ="INSERT INTO post_like( liker_id, post_id)"+
                " VALUES ("+this.liker_id+","+this.post_id+")";
        this.id = this.insertData(query);
        return this.id;
    }
    public int delete(){

        String query ="DELETE FROM post_like where liker_id = "+this.liker_id+" and  post_id = "+this.post_id+" limit 1";
        return  this.deleteData(query);
    }
    public boolean isAlreadyLiked(){
        String query = "select id  from post_like where post_like.post_id = "+this.post_id+" and liker_id = "+this.liker_id+" limit 1";
        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }
        return false;
    }
    public int getLikeCountByPostId(){
        String query = "select count(id) as likeCount from post_like where post_like.post_id = "+this.post_id;
        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
               return this.resultSet.getInt("likeCount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }
        return 0;
    }
    public ArrayList<Liker> getLikersByPostId(){
        ArrayList<Liker> likerList = new ArrayList<Liker>();
        String query =  " select post_like.id,post_like.created_date," +
                " app_login_credential.id as app_login_credentialId, app_login_credential.text_status, app_login_credential.phone_number, app_login_credential.created_date as app_lCdate," +
                " user_inf.id as user_infId, user_inf.f_name, user_inf.l_name, user_inf.pic_path as proPic, user_inf.address_id, user_inf.created_date as user_infCdate," +
                " location.id as locationId, location.lat, location.lng, location.formatted_address, location.country, location.created_date as locationCDate" +
                " FROM post_like " +
                " join app_login_credential on app_login_credential.id = post_like.liker_id " +
                " join user_inf on user_inf.id = app_login_credential.u_id " +
                " left join location on location.id = user_inf.address_id " +
                " where post_like.post_id = "+this.post_id;

        query += " order by post_like.id DESC ";
        if(this.limit >0){
            this.offset = this.offset * this.limit;
            query += " LIMIT "+this.offset+" ,"+this.limit+" ";
        }
        System.out.println(query);
        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                Liker liker = new Liker();

                liker.likeId = this.resultSet.getInt("post_like.id");
                liker.likedDate = this.resultSet.getString("post_like.created_date");

                liker.id = this.resultSet.getInt("app_login_credentialId");
                liker.textStatus = this.resultSet.getString("text_status");
                liker.phoneNumber = this.resultSet.getString("phone_number");
                liker.createdDate = this.resultSet.getString("app_lCdate");

                liker.user.id = this.resultSet.getInt("user_infId");
                liker.user.firstName = this.resultSet.getString("f_name");
                liker.user.lastName = this.resultSet.getString("l_name");

                try{
                    liker.user.picPath  = this.gson.fromJson(this.resultSet.getString("proPic"), Pictures.class);
                }catch (Exception ex){
                    liker.user.picPath.original.path = this.resultSet.getString("proPic");

                    ex.printStackTrace();
                }
                liker.user.address.id = (this.resultSet.getObject("locationId")==null)?0:this.resultSet.getInt("locationId");
                liker.user.address.lat = (this.resultSet.getObject("lat")==null)?0:this.resultSet.getDouble("lat");
                liker.user.address.lng = (this.resultSet.getObject("lng")==null)?0:this.resultSet.getDouble("lng");
                liker.user.address.formattedAddress = (this.resultSet.getObject("formatted_address")==null)?"":this.resultSet.getString("formatted_address");
                liker.user.address.countryName = (this.resultSet.getObject("country")==null)?"":this.resultSet.getString("country");
                liker.user.address.createdDate = (this.resultSet.getObject("locationCDate")==null)?"":this.resultSet.getString("locationCDate");


                likerList.add(liker);


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return likerList;

    }
}
