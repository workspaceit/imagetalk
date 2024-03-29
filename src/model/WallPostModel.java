package model;

import com.google.gson.Gson;
import model.datamodel.app.WallPost;
import model.datamodel.photo.Pictures;

import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by mi on 10/2/15.
 */
public class WallPostModel extends ImageTalkBaseModel{


    private int id;
    private int owner_id;
    private String  description;
    private int type;
    private String picture_path;
    private int location_id;
    private String wall_post_mood;
    private int comment_count;
    private String created_date;
    private Gson gson;
    public WallPostModel(){
        super();
        super.tableName = "wall_post";


        this.id =0;
        this.owner_id=0;
        this.description="";
        this.picture_path=null;
        this.location_id=0;
        this.wall_post_mood="";
        this.created_date="";

        this.gson = new Gson();

    }

    public int getId() {
        return id;
    }

    public boolean setId(int id) {
        this.id = id;
        return true;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public boolean setOwner_id(int owner_id) {
        this.owner_id = owner_id;
        return true;
    }

    public int getCommentCount() {
        return comment_count;
    }

    public boolean setCommentCount(int comment_count) {
        this.comment_count = comment_count;
        return true;
    }

    public String getDescrption() {
        return description;
    }
    public String getWallPostMood() {
        return wall_post_mood;
    }

    public boolean setDescrption(String description) {

        System.out.println("description " + description);
        description = description.trim();
        System.out.println("description.trim()" + description.trim());

        try {
            this.description = new String(description.getBytes("UTF-8"),"UTF-8"); //StringEscapeUtils.escapeEcmaScript( description.trim());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(" this.description" +  this.description);
        return true;
    }

    public boolean setWallPostMood(String wall_post_mood) {

        System.out.println("WallPostMood " + wall_post_mood);
        wall_post_mood = wall_post_mood.trim();
        System.out.println("wall_post_mood.trim()" + wall_post_mood.trim());

        try {
            this.wall_post_mood = new String(wall_post_mood.getBytes("UTF-8"),"UTF-8"); //StringEscapeUtils.escapeEcmaScript( description.trim());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(" this.wall_post_mood" +  this.wall_post_mood);
        return true;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPicture_path() {
        return picture_path;
    }

    public boolean setPicture_path(String picture_path) {
        this.picture_path = picture_path;
        return true;
    }

    public int getLocation_id() {
        return location_id;
    }

    public boolean setLocation_id(int location_id) {
        this.location_id = location_id;
        return true;
    }

    public String getCreated_date() {
        return created_date;
    }

    public boolean setCreated_date(String created_date) {
        this.created_date = created_date;
        return true;
    }


    public boolean isWallPostOwner(){
        String query = "SELECT id from wall_post where owner_id="+this.owner_id+" and id = "+this.id;

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
    public ArrayList<WallPost> getAllFavoriteByOwnerId(){
        ArrayList<WallPost> wallPostList = new ArrayList<WallPost>();

        String query = "SELECT wall_post.id,wall_post.owner_id,wall_post.type as postType,wall_post.description,wall_post.wall_post_mood,wall_post.picture_path,wall_post.location_id,wall_post.created_date,wall_post.created_date as wall_postCdate, " +

                " (select count(id) from post_like where post_like.post_id = wall_post.id ) as likeCount," +
                " (select count(id) from post_comment where post_comment.post_id = wall_post.id ) as commentCount," +
                " (select count(id) from post_like where post_like.post_id =  wall_post.id  and liker_id = "+this.getCurrentUserId()+" limit 1 ) as isLiked," +
                " (select count(id) from wall_post_favorite where wall_post_favorite.wall_post_id = wall_post.id and owner_id = "+this.getCurrentUserId()+" limit 1 ) as isFavorite," +
                " app_login_credential.id as app_login_credentialId, app_login_credential.text_status, app_login_credential.phone_number, app_login_credential.created_date as app_lCdate," +
                " user_inf.id as user_infId, user_inf.f_name, user_inf.l_name, user_inf.pic_path as proPic, user_inf.address_id, user_inf.created_date as user_infCdate," +
                " location.id as locationId, location.lat, location.lng, location.formatted_address, location.country, location.created_date as locationCDate," +
                " postLoc.*,job.* " +
                " FROM wall_post " +
                " join wall_post_favorite on wall_post_favorite.wall_post_id = wall_post.id " +
                " join app_login_credential on app_login_credential.id = wall_post.owner_id " +
                " join user_inf on user_inf.id = app_login_credential.u_id " +
                " left join location on location.id = user_inf.address_id " +
                " left join job on job.app_login_credential_id = app_login_credential.id " +
                " left join location as postLoc on postLoc.id = wall_post.location_id " +
                " where wall_post.is_blocked = 0 and " +
                " wall_post_favorite.owner_id = "+this.owner_id;

        query += " order by  wall_post.id  DESC ";
        if(this.limit >0){
            this.offset = this.offset * this.limit;
            query += " LIMIT "+this.offset+" ,"+this.limit+" ";
        }
        System.out.println(query);
        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                WallPost wallPost = new WallPost();
                wallPost.id = this.resultSet.getInt("wall_post.id");
                wallPost.description = this.resultSet.getString("description");
                wallPost.wallPostMood = this.resultSet.getString("wall_post_mood");
                wallPost.type = this.resultSet.getInt("postType");
                wallPost.picPath = this.resultSet.getString("wall_post.picture_path");
                wallPost.createdDate = this.getProcessedDateTime(this.resultSet.getString("wall_postCdate")); //Long.toString(this.resultSet.getString("wall_postCdate").getTime());
                wallPost.isLiked = (this.resultSet.getInt("isLiked")==1)?true:false;
                wallPost.isFavorite = (this.resultSet.getInt("isFavorite")==1)?true:false;
                wallPost.likeCount = this.resultSet.getInt("likeCount");
                wallPost.commentCount = this.resultSet.getInt("commentCount");

                wallPost.owner.id = this.resultSet.getInt("app_login_credentialId");
                wallPost.owner.textStatus = this.resultSet.getString("text_status");
                wallPost.owner.phoneNumber = this.resultSet.getString("phone_number");
                wallPost.owner.createdDate = this.resultSet.getString("app_lCdate");

                wallPost.owner.user.id = this.resultSet.getInt("user_infId");
                wallPost.owner.user.firstName = this.resultSet.getString("f_name");
                wallPost.owner.user.lastName = this.resultSet.getString("l_name");

                try{
                    wallPost.owner.user.picPath  = this.gson.fromJson(this.resultSet.getString("proPic"), Pictures.class);
                }catch (Exception ex){
                    wallPost.owner.user.picPath.original.path = this.resultSet.getString("proPic");

                    ex.printStackTrace();
                }
                wallPost.owner.user.address.id = (this.resultSet.getObject("locationId")==null)?0:this.resultSet.getInt("locationId");
                wallPost.owner.user.address.lat = (this.resultSet.getObject("lat")==null)?0:this.resultSet.getDouble("lat");
                wallPost.owner.user.address.lng = (this.resultSet.getObject("lng")==null)?0:this.resultSet.getDouble("lng");
                wallPost.owner.user.address.formattedAddress = (this.resultSet.getObject("formatted_address")==null)?"":this.resultSet.getString("formatted_address");
                wallPost.owner.user.address.countryName = (this.resultSet.getObject("country")==null)?"":this.resultSet.getString("country");
                wallPost.owner.user.address.createdDate = (this.resultSet.getObject("locationCDate")==null)?"":this.resultSet.getString("locationCDate");

                wallPost.places.id = (this.resultSet.getObject("postLoc.id")==null)?0:this.resultSet.getInt("postLoc.id");
                wallPost.places.placeId = (this.resultSet.getObject("postLoc.place_id")==null)?"":this.resultSet.getString("postLoc.place_id");
                wallPost.places.icon = (this.resultSet.getObject("postLoc.icon")==null)?"":this.resultSet.getString("postLoc.icon");
                wallPost.places.name = (this.resultSet.getObject("postLoc.name")==null)?"":this.resultSet.getString("postLoc.name");
                wallPost.places.googlePlaceId = (this.resultSet.getObject("postLoc.google_place_id")==null)?"":this.resultSet.getString("postLoc.google_place_id");

                wallPost.places.lat = (this.resultSet.getObject("postLoc.lat")==null)?0:this.resultSet.getDouble("postLoc.lat");
                wallPost.places.lng = (this.resultSet.getObject("postLoc.lng")==null)?0:this.resultSet.getDouble("postLoc.lng");
                wallPost.places.formattedAddress = (this.resultSet.getObject("postLoc.formatted_address")==null)?"":this.resultSet.getString("postLoc.formatted_address");
                wallPost.places.countryName = (this.resultSet.getObject("postLoc.country")==null)?"":this.resultSet.getString("postLoc.country");
                wallPost.places.createdDate = (this.resultSet.getObject("postLoc.created_date")==null)?"":this.resultSet.getString("postLoc.created_date");

                //job details
                wallPost.owner.job.id =(this.resultSet.getObject("job.id")==null)?0:this.resultSet.getInt("job.id");
                wallPost.owner.job.appCredentialId = (this.resultSet.getObject("job.app_login_credential_id")==null)?0:this.resultSet.getInt("job.app_login_credential_id");
                wallPost.owner.job.title = (this.resultSet.getObject("job.title") == null) ? "" : this.resultSet.getString("job.title");
                wallPost.owner.job.description = (this.resultSet.getObject("job.description") == null)? "" : this.resultSet.getString("job.description");
                try{
                    wallPost.owner.job.icons = (this.resultSet.getObject("job.icon")==null || !this.resultSet.getString("job.icon").trim().startsWith("{"))?new Pictures():this.gson.fromJson(this.resultSet.getString("job.icon"),Pictures.class);

                }catch (Exception ex){
                    ex.printStackTrace();
                }
                wallPost.owner.job.price = (this.resultSet.getObject("job.price") == null)?0:this.resultSet.getFloat("job.price");
                wallPost.owner.job.paymentType = (this.resultSet.getObject("job.payment_type") == null)?0:this.resultSet.getInt("job.payment_type");
                try {
                    wallPost.owner.job.createdDate = (this.resultSet.getObject("job.created_date") == null)?"":this.getProcessedDateTime(this.resultSet.getString("job.created_date"));
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                    wallPost.owner.job.createdDate = "";
                }
                //end job details

                wallPost = this.getOtherDependency(wallPost);

                wallPostList.add(wallPost);


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return wallPostList;

    }
    public ArrayList<WallPost> getAllRecent(){
        ArrayList<WallPost> wallPostList = new ArrayList<WallPost>();

        String query = "SELECT wall_post.id,wall_post.owner_id,wall_post.type as postType,wall_post.description,wall_post.wall_post_mood,wall_post.comment_count,wall_post.picture_path,wall_post.location_id,wall_post.created_date as wall_postCdate, " +

                " (select count(id) from post_like where post_like.post_id = wall_post.id ) as likeCount," +
                " (select count(id) from post_comment where post_comment.post_id = wall_post.id ) as commentCount," +
                " (select count(id) from post_like where post_like.post_id =  wall_post.id  and liker_id = "+this.getCurrentUserId()+" limit 1 ) as isLiked," +
                " (select count(id) from wall_post_favorite where wall_post_favorite.wall_post_id = wall_post.id and owner_id = "+this.getCurrentUserId()+" limit 1 ) as isFavorite," +
                " app_login_credential.id as app_login_credentialId, app_login_credential.text_status, app_login_credential.phone_number, app_login_credential.created_date as app_lCdate," +
                " user_inf.id as user_infId, user_inf.f_name, user_inf.l_name, user_inf.pic_path as proPic, user_inf.address_id, user_inf.created_date as user_infCdate," +
                " location.id as locationId, location.lat, location.lng, location.formatted_address, location.country, location.created_date as locationCDate," +
                " postLoc.*,job.*" +
                " FROM wall_post " +
                " join app_login_credential on app_login_credential.id = wall_post.owner_id " +
                " join user_inf on user_inf.id = app_login_credential.u_id " +
                " left join location on location.id = user_inf.address_id " +
                " left join job on job.app_login_credential_id = app_login_credential.id " +
                " left join location as postLoc on postLoc.id = wall_post.location_id "+
                " where wall_post.is_blocked = 0 and " +
                " wall_post.id NOT IN (SELECT wall_post_id FROM wall_post_status WHERE owner_id="+this.getCurrentUserId()+")";

        query += " order by  wall_post.id  DESC ";
        //System.out.println(query);
        //System.out.println("app cred id in wallpost: "+ this.getCurrentUserId());
        if(this.limit >0){
            this.offset = this.offset * this.limit;
            query += " LIMIT "+this.offset+" ,"+this.limit+" ";
        }

        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                WallPost wallPost = new WallPost();
                wallPost.id = this.resultSet.getInt("wall_post.id");
                wallPost.description = this.resultSet.getString("description");
                wallPost.wallPostMood = this.resultSet.getString("wall_post_mood");
                wallPost.commentCount = this.resultSet.getInt("comment_count");
                wallPost.type = this.resultSet.getInt("postType");
                wallPost.picPath = this.resultSet.getString("wall_post.picture_path");
                wallPost.createdDate = this.getProcessedDateTime(this.resultSet.getString("wall_postCdate")); //Long.toString(this.resultSet.getString("wall_postCdate").getTime());
                wallPost.isLiked = (this.resultSet.getInt("isLiked")==1)?true:false;
                wallPost.isFavorite = (this.resultSet.getInt("isFavorite")==1)?true:false;
                wallPost.likeCount = this.resultSet.getInt("likeCount");
                wallPost.commentCount = this.resultSet.getInt("commentCount");

                wallPost.owner.id = this.resultSet.getInt("app_login_credentialId");
                wallPost.owner.textStatus = this.resultSet.getString("text_status");
                wallPost.owner.phoneNumber = this.resultSet.getString("phone_number");
                wallPost.owner.createdDate = this.resultSet.getString("app_lCdate");

                wallPost.owner.user.id = this.resultSet.getInt("user_infId");
                wallPost.owner.user.firstName = this.resultSet.getString("f_name");
                wallPost.owner.user.lastName = this.resultSet.getString("l_name");

                try{
                    wallPost.owner.user.picPath  = this.gson.fromJson(this.resultSet.getString("proPic"), Pictures.class);
                }catch (Exception ex){
                    wallPost.owner.user.picPath.original.path = this.resultSet.getString("proPic");

                    ex.printStackTrace();
                }
                wallPost.owner.user.address.id = (this.resultSet.getObject("locationId")==null)?0:this.resultSet.getInt("locationId");
                wallPost.owner.user.address.lat = (this.resultSet.getObject("lat")==null)?0:this.resultSet.getDouble("lat");
                wallPost.owner.user.address.lng = (this.resultSet.getObject("lng")==null)?0:this.resultSet.getDouble("lng");
                wallPost.owner.user.address.formattedAddress = (this.resultSet.getObject("formatted_address")==null)?"":this.resultSet.getString("formatted_address");
                wallPost.owner.user.address.countryName = (this.resultSet.getObject("country")==null)?"":this.resultSet.getString("country");
                wallPost.owner.user.address.createdDate = (this.resultSet.getObject("locationCDate")==null)?"":this.resultSet.getString("locationCDate");

                wallPost.places.id = (this.resultSet.getObject("postLoc.id")==null)?0:this.resultSet.getInt("postLoc.id");
                wallPost.places.placeId = (this.resultSet.getObject("postLoc.place_id")==null)?"":this.resultSet.getString("postLoc.place_id");
                wallPost.places.icon = (this.resultSet.getObject("postLoc.icon")==null)?"":this.resultSet.getString("postLoc.icon");
                wallPost.places.name = (this.resultSet.getObject("postLoc.name")==null)?"":this.resultSet.getString("postLoc.name");
                wallPost.places.googlePlaceId = (this.resultSet.getObject("postLoc.google_place_id")==null)?"":this.resultSet.getString("postLoc.google_place_id");

                wallPost.places.lat = (this.resultSet.getObject("postLoc.lat")==null)?0:this.resultSet.getDouble("postLoc.lat");
                wallPost.places.lng = (this.resultSet.getObject("postLoc.lng")==null)?0:this.resultSet.getDouble("postLoc.lng");
                wallPost.places.formattedAddress = (this.resultSet.getObject("postLoc.formatted_address")==null)?"":this.resultSet.getString("postLoc.formatted_address");
                wallPost.places.countryName = (this.resultSet.getObject("postLoc.country")==null)?"":this.resultSet.getString("postLoc.country");
                wallPost.places.createdDate = (this.resultSet.getObject("postLoc.created_date")==null)?"":this.resultSet.getString("postLoc.created_date");

                //job details
                wallPost.owner.job.id =(this.resultSet.getObject("job.id")==null)?0:this.resultSet.getInt("job.id");
                wallPost.owner.job.appCredentialId = (this.resultSet.getObject("job.app_login_credential_id")==null)?0:this.resultSet.getInt("job.app_login_credential_id");
                wallPost.owner.job.title = (this.resultSet.getObject("job.title") == null) ? "" : this.resultSet.getString("job.title");
                wallPost.owner.job.description = (this.resultSet.getObject("job.description") == null)? "" : this.resultSet.getString("job.description");
                try{
                    wallPost.owner.job.icons = (this.resultSet.getObject("job.icon")==null || !this.resultSet.getString("job.icon").trim().startsWith("{"))?new Pictures():this.gson.fromJson(this.resultSet.getString("job.icon"),Pictures.class);

                }catch (Exception ex){
                    ex.printStackTrace();
                }
                wallPost.owner.job.price = (this.resultSet.getObject("job.price") == null)?0:this.resultSet.getFloat("job.price");
                wallPost.owner.job.paymentType = (this.resultSet.getObject("job.payment_type") == null)?0:this.resultSet.getInt("job.payment_type");
                try {
                    wallPost.owner.job.createdDate = (this.resultSet.getObject("job.created_date") == null)?"":this.getProcessedDateTime(this.resultSet.getString("job.created_date"));
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                    wallPost.owner.job.createdDate = "";
                }
                //end job details

                wallPost = this.getOtherDependency(wallPost);

                wallPostList.add(wallPost);


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return wallPostList;

    }
    public WallPost getById(){
        WallPost wallPost = new WallPost();
        String query = "SELECT wall_post.id,wall_post.type as postType,wall_post.owner_id,wall_post.description,wall_post.wall_post_mood,wall_post.picture_path,wall_post.location_id,wall_post.created_date as wall_postCdate, " +

                " (select count(id) from post_like where post_like.post_id = wall_post.id ) as likeCount," +
                " (select count(id) from post_comment where post_comment.post_id = wall_post.id ) as commentCount," +
                " (select count(id) from post_like where post_like.post_id =  wall_post.id  and liker_id = "+this.getCurrentUserId()+" limit 1 ) as isLiked," +
                " (select count(id) from wall_post_favorite where wall_post_favorite.wall_post_id = wall_post.id and owner_id = "+this.getCurrentUserId()+" limit 1 ) as isFavorite," +
                " app_login_credential.id as app_login_credentialId, app_login_credential.text_status, app_login_credential.phone_number, app_login_credential.created_date as app_lCdate," +
                " user_inf.id as user_infId, user_inf.f_name, user_inf.l_name, user_inf.pic_path as proPic, user_inf.address_id, user_inf.created_date as user_infCdate," +
                " location.id as locationId, location.lat, location.lng, location.formatted_address, location.country, location.created_date as locationCDate," +
                " postLoc.*,job.*" +
                " FROM wall_post " +
                " join app_login_credential on app_login_credential.id = wall_post.owner_id " +
                " join user_inf on user_inf.id = app_login_credential.u_id " +
                " left join location on location.id = user_inf.address_id " +
                " left join job on job.app_login_credential_id = app_login_credential.id " +
                " left join location as postLoc on postLoc.id = wall_post.location_id " +
                " where wall_post.is_blocked = 0 and " +
                " wall_post.id = "+this.id+" limit 1";



        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                wallPost.id = this.resultSet.getInt("wall_post.id");
                wallPost.description = this.resultSet.getString("description");
                wallPost.wallPostMood = this.resultSet.getString("wall_post_mood");
                wallPost.type = this.resultSet.getInt("postType");
                wallPost.picPath = this.resultSet.getString("wall_post.picture_path");
                wallPost.likeCount = this.resultSet.getInt("likeCount");
                wallPost.commentCount = this.resultSet.getInt("commentCount");

                wallPost.createdDate = this.getProcessedDateTime(this.resultSet.getString("wall_postCdate"));
                wallPost.isLiked = (this.resultSet.getInt("isLiked")==1)?true:false;
                wallPost.isFavorite = (this.resultSet.getInt("isFavorite")==1)?true:false;

                wallPost.owner.id = this.resultSet.getInt("app_login_credentialId");
                wallPost.owner.textStatus = this.resultSet.getString("text_status");
                wallPost.owner.phoneNumber = this.resultSet.getString("phone_number");
                wallPost.owner.createdDate = this.resultSet.getString("app_lCdate");

                wallPost.owner.user.id = this.resultSet.getInt("user_infId");
                wallPost.owner.user.firstName = this.resultSet.getString("f_name");
                wallPost.owner.user.lastName = this.resultSet.getString("l_name");

                try{
                    wallPost.owner.user.picPath  = this.gson.fromJson(this.resultSet.getString("proPic"), Pictures.class);
                }catch (Exception ex){
                    wallPost.owner.user.picPath.original.path = this.resultSet.getString("proPic");

                    ex.printStackTrace();
                }

                wallPost.owner.user.address.id = (this.resultSet.getObject("locationId")==null)?0:this.resultSet.getInt("locationId");
                wallPost.owner.user.address.lat = (this.resultSet.getObject("lat")==null)?0:this.resultSet.getDouble("lat");
                wallPost.owner.user.address.lng = (this.resultSet.getObject("lng")==null)?0:this.resultSet.getDouble("lng");
                wallPost.owner.user.address.formattedAddress = (this.resultSet.getObject("formatted_address")==null)?"":this.resultSet.getString("formatted_address");
                wallPost.owner.user.address.countryName = (this.resultSet.getObject("country")==null)?"":this.resultSet.getString("country");
                wallPost.owner.user.address.createdDate = (this.resultSet.getObject("locationCDate")==null)?"":this.resultSet.getString("locationCDate");

                wallPost.places.id = (this.resultSet.getObject("postLoc.id")==null)?0:this.resultSet.getInt("postLoc.id");
                wallPost.places.placeId = (this.resultSet.getObject("postLoc.place_id")==null)?"":this.resultSet.getString("postLoc.place_id");
                wallPost.places.icon = (this.resultSet.getObject("postLoc.icon")==null)?"":this.resultSet.getString("postLoc.icon");
                wallPost.places.name = (this.resultSet.getObject("postLoc.name")==null)?"":this.resultSet.getString("postLoc.name");
                wallPost.places.googlePlaceId = (this.resultSet.getObject("postLoc.google_place_id")==null)?"":this.resultSet.getString("postLoc.google_place_id");

                wallPost.places.lat = (this.resultSet.getObject("postLoc.lat")==null)?0:this.resultSet.getDouble("postLoc.lat");
                wallPost.places.lng = (this.resultSet.getObject("postLoc.lng")==null)?0:this.resultSet.getDouble("postLoc.lng");
                wallPost.places.formattedAddress = (this.resultSet.getObject("postLoc.formatted_address")==null)?"":this.resultSet.getString("postLoc.formatted_address");
                wallPost.places.countryName = (this.resultSet.getObject("postLoc.country")==null)?"":this.resultSet.getString("postLoc.country");
                wallPost.places.createdDate = (this.resultSet.getObject("postLoc.created_date")==null)?"":this.resultSet.getString("postLoc.created_date");

                //job details
                wallPost.owner.job.id =(this.resultSet.getObject("job.id")==null)?0:this.resultSet.getInt("job.id");
                wallPost.owner.job.appCredentialId = (this.resultSet.getObject("job.app_login_credential_id")==null)?0:this.resultSet.getInt("job.app_login_credential_id");
                wallPost.owner.job.title = (this.resultSet.getObject("job.title") == null) ? "" : this.resultSet.getString("job.title");
                wallPost.owner.job.description = (this.resultSet.getObject("job.description") == null)? "" : this.resultSet.getString("job.description");
                try{
                    wallPost.owner.job.icons = (this.resultSet.getObject("job.icon")==null || !this.resultSet.getString("job.icon").trim().startsWith("{"))?new Pictures():this.gson.fromJson(this.resultSet.getString("job.icon"),Pictures.class);

                }catch (Exception ex){
                    ex.printStackTrace();
                }
                wallPost.owner.job.price = (this.resultSet.getObject("job.price") == null)?0:this.resultSet.getFloat("job.price");
                wallPost.owner.job.paymentType = (this.resultSet.getObject("job.payment_type") == null)?0:this.resultSet.getInt("job.payment_type");
                try {
                    wallPost.owner.job.createdDate = (this.resultSet.getObject("job.created_date") == null)?"":this.getProcessedDateTime(this.resultSet.getString("job.created_date"));
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                    wallPost.owner.job.createdDate = "";
                }
                //end job details

                wallPost = getOtherDependency(wallPost);


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return wallPost;

    }

    public WallPost getByIdForNotification()
    {
        WallPost wallPost = new WallPost();
        String query = "SELECT wall_post.id,wall_post.type as postType,wall_post.owner_id,wall_post.description,wall_post.wall_post_mood,wall_post.picture_path,wall_post.location_id,wall_post.created_date as wall_postCdate, " +

                       " (select count(id) from post_like where post_like.post_id = wall_post.id ) as likeCount," +
                       " (select count(id) from post_comment where post_comment.post_id = wall_post.id ) as commentCount," +
                       " (select count(id) from post_like where post_like.post_id =  wall_post.id  and liker_id = "+this.getCurrentUserId()+" limit 1 ) as isLiked," +
                       " (select count(id) from wall_post_favorite where wall_post_favorite.wall_post_id = wall_post.id and owner_id = "+this.getCurrentUserId()+" limit 1 ) as isFavorite," +
                       " app_login_credential.id as app_login_credentialId, app_login_credential.text_status, app_login_credential.phone_number, app_login_credential.created_date as app_lCdate," +
                       " user_inf.id as user_infId, user_inf.f_name, user_inf.l_name, user_inf.pic_path as proPic, user_inf.address_id, user_inf.created_date as user_infCdate," +
                       " location.id as locationId, location.lat, location.lng, location.formatted_address, location.country, location.created_date as locationCDate," +
                       " postLoc.*,job.*" +
                       " FROM wall_post " +
                       " join app_login_credential on app_login_credential.id = wall_post.owner_id " +
                       " join user_inf on user_inf.id = app_login_credential.u_id " +
                       " left join location on location.id = user_inf.address_id " +
                       " left join job on job.app_login_credential_id = app_login_credential.id " +
                       " left join location as postLoc on postLoc.id = wall_post.location_id " +
                       " where wall_post.is_blocked = 0 and " +
                       " wall_post.id = "+this.id+" limit 1";



        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                wallPost.id = this.resultSet.getInt("wall_post.id");
                wallPost.description = this.resultSet.getString("description");
                wallPost.wallPostMood = this.resultSet.getString("wall_post_mood");
                wallPost.type = this.resultSet.getInt("postType");
                wallPost.picPath = this.resultSet.getString("wall_post.picture_path");
                wallPost.likeCount = this.resultSet.getInt("likeCount");
                wallPost.commentCount = this.resultSet.getInt("commentCount");

                wallPost.createdDate = this.getProcessedDateTime(this.resultSet.getString("wall_postCdate"));
                wallPost.isLiked = (this.resultSet.getInt("isLiked")==1)?true:false;
                wallPost.isFavorite = (this.resultSet.getInt("isFavorite")==1)?true:false;

                wallPost.owner.id = this.resultSet.getInt("app_login_credentialId");
                wallPost.owner.textStatus = this.resultSet.getString("text_status");
                wallPost.owner.phoneNumber = this.resultSet.getString("phone_number");
                wallPost.owner.createdDate = this.resultSet.getString("app_lCdate");

                wallPost.owner.user.id = this.resultSet.getInt("user_infId");
                wallPost.owner.user.firstName = this.resultSet.getString("f_name");
                wallPost.owner.user.lastName = this.resultSet.getString("l_name");

                try{
                    wallPost.owner.user.picPath  = this.gson.fromJson(this.resultSet.getString("proPic"), Pictures.class);
                }catch (Exception ex){
                    wallPost.owner.user.picPath.original.path = this.resultSet.getString("proPic");

                    ex.printStackTrace();
                }

                wallPost.owner.user.address.id = (this.resultSet.getObject("locationId")==null)?0:this.resultSet.getInt("locationId");
                wallPost.owner.user.address.lat = (this.resultSet.getObject("lat")==null)?0:this.resultSet.getDouble("lat");
                wallPost.owner.user.address.lng = (this.resultSet.getObject("lng")==null)?0:this.resultSet.getDouble("lng");
                wallPost.owner.user.address.formattedAddress = (this.resultSet.getObject("formatted_address")==null)?"":this.resultSet.getString("formatted_address");
                wallPost.owner.user.address.countryName = (this.resultSet.getObject("country")==null)?"":this.resultSet.getString("country");
                wallPost.owner.user.address.createdDate = (this.resultSet.getObject("locationCDate")==null)?"":this.resultSet.getString("locationCDate");

                wallPost.places.id = (this.resultSet.getObject("postLoc.id")==null)?0:this.resultSet.getInt("postLoc.id");
                wallPost.places.placeId = (this.resultSet.getObject("postLoc.place_id")==null)?"":this.resultSet.getString("postLoc.place_id");
                wallPost.places.icon = (this.resultSet.getObject("postLoc.icon")==null)?"":this.resultSet.getString("postLoc.icon");
                wallPost.places.name = (this.resultSet.getObject("postLoc.name")==null)?"":this.resultSet.getString("postLoc.name");
                wallPost.places.googlePlaceId = (this.resultSet.getObject("postLoc.google_place_id")==null)?"":this.resultSet.getString("postLoc.google_place_id");

                wallPost.places.lat = (this.resultSet.getObject("postLoc.lat")==null)?0:this.resultSet.getDouble("postLoc.lat");
                wallPost.places.lng = (this.resultSet.getObject("postLoc.lng")==null)?0:this.resultSet.getDouble("postLoc.lng");
                wallPost.places.formattedAddress = (this.resultSet.getObject("postLoc.formatted_address")==null)?"":this.resultSet.getString("postLoc.formatted_address");
                wallPost.places.countryName = (this.resultSet.getObject("postLoc.country")==null)?"":this.resultSet.getString("postLoc.country");
                wallPost.places.createdDate = (this.resultSet.getObject("postLoc.created_date")==null)?"":this.resultSet.getString("postLoc.created_date");

                //job details
                wallPost.owner.job.id =(this.resultSet.getObject("job.id")==null)?0:this.resultSet.getInt("job.id");
                wallPost.owner.job.appCredentialId = (this.resultSet.getObject("job.app_login_credential_id")==null)?0:this.resultSet.getInt("job.app_login_credential_id");
                wallPost.owner.job.title = (this.resultSet.getObject("job.title") == null) ? "" : this.resultSet.getString("job.title");
                wallPost.owner.job.description = (this.resultSet.getObject("job.description") == null)? "" : this.resultSet.getString("job.description");
                try{
                    wallPost.owner.job.icons = (this.resultSet.getObject("job.icon")==null || !this.resultSet.getString("job.icon").trim().startsWith("{"))?new Pictures():this.gson.fromJson(this.resultSet.getString("job.icon"),Pictures.class);

                }catch (Exception ex){
                    ex.printStackTrace();
                }
                wallPost.owner.job.price = (this.resultSet.getObject("job.price") == null)?0:this.resultSet.getFloat("job.price");
                wallPost.owner.job.paymentType = (this.resultSet.getObject("job.payment_type") == null)?0:this.resultSet.getInt("job.payment_type");
                try {
                    wallPost.owner.job.createdDate = (this.resultSet.getObject("job.created_date") == null)?"":this.getProcessedDateTime(this.resultSet.getString("job.created_date"));
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                    wallPost.owner.job.createdDate = "";
                }
                //end job details

                wallPost = getOtherDependency(wallPost);


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return wallPost;
    }

    public int getCountByOwnerId(){
        String query = "SELECT count(wall_post.id) as postCount  FROM wall_post " +
                " where wall_post.owner_id = "+this.owner_id;
        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {

                return this.resultSet.getInt("postCount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return 0;
    }

    public ArrayList<WallPost> getByOwner_id(){
        ArrayList<WallPost> wallPostList = new ArrayList<WallPost>();

        String query = "SELECT wall_post.id,wall_post.owner_id,wall_post.description,wall_post.wall_post_mood,wall_post.type as postType,wall_post.picture_path,wall_post.location_id,wall_post.created_date as wall_postCdate, " +

                " (select count(id) from post_like where post_like.post_id = wall_post.id ) as likeCount," +
                " (select count(id) from post_comment where post_comment.post_id = wall_post.id ) as commentCount," +
                " (select count(id) from post_like where post_like.post_id =  wall_post.id  and liker_id = "+this.getCurrentUserId()+" limit 1 ) as isLiked," +
                " (select count(id) from wall_post_favorite where wall_post_favorite.wall_post_id = wall_post.id and owner_id = "+this.getCurrentUserId()+" limit 1 ) as isFavorite," +
                " app_login_credential.id as app_login_credentialId, app_login_credential.text_status, app_login_credential.phone_number, app_login_credential.created_date as app_lCdate," +
                " user_inf.id as user_infId, user_inf.f_name, user_inf.l_name, user_inf.pic_path as proPic, user_inf.address_id, user_inf.created_date as user_infCdate," +
                " location.id as locationId, location.lat, location.lng, location.formatted_address, location.country, location.created_date as locationCDate," +
                " postLoc.*,job.*" +
                " FROM wall_post " +
                " join app_login_credential on app_login_credential.id = wall_post.owner_id " +
                " join user_inf on user_inf.id = app_login_credential.u_id " +
                " left join location on location.id = user_inf.address_id " +
                " left join job on job.app_login_credential_id = app_login_credential.id " +
                " left join location as postLoc on postLoc.id = wall_post.location_id " +
                " where wall_post.is_blocked = 0 and " +
                " wall_post.owner_id = "+this.owner_id;
        query += " order by  wall_post.id  DESC ";

        if(this.limit >0){
            this.offset = this.offset * this.limit;
            query += " LIMIT "+this.offset+" ,"+this.limit+" ";
        }
        System.out.println(query);
        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                WallPost wallPost = new WallPost();
                wallPost.id = this.resultSet.getInt("wall_post.id");
                wallPost.description = this.resultSet.getString("description");
                wallPost.wallPostMood = this.resultSet.getString("wall_post_mood");
                wallPost.type = this.resultSet.getInt("postType");
                wallPost.picPath = this.resultSet.getString("wall_post.picture_path");
                wallPost.isLiked = (this.resultSet.getInt("isLiked")==1)?true:false;
                wallPost.isLiked = (this.resultSet.getInt("isFavorite")==1)?true:false;
                wallPost.likeCount = this.resultSet.getInt("likeCount");
                wallPost.commentCount = this.resultSet.getInt("commentCount");
                wallPost.createdDate = this.getProcessedDateTime(this.resultSet.getString("wall_postCdate"));

                wallPost.owner.id = this.resultSet.getInt("app_login_credentialId");
                wallPost.owner.textStatus = this.resultSet.getString("text_status");
                wallPost.owner.phoneNumber = this.resultSet.getString("phone_number");
                wallPost.owner.createdDate = this.resultSet.getString("app_lCdate");

                wallPost.owner.user.id = this.resultSet.getInt("user_infId");
                wallPost.owner.user.firstName = this.resultSet.getString("f_name");
                wallPost.owner.user.lastName = this.resultSet.getString("l_name");

                try{
                    wallPost.owner.user.picPath  = this.gson.fromJson(this.resultSet.getString("proPic"), Pictures.class);
                }catch (Exception ex){
                    wallPost.owner.user.picPath.original.path = this.resultSet.getString("proPic");

                    ex.printStackTrace();
                }
                wallPost.owner.user.address.id = (this.resultSet.getObject("locationId")==null)?0:this.resultSet.getInt("locationId");
                wallPost.owner.user.address.lat = (this.resultSet.getObject("lat")==null)?0:this.resultSet.getDouble("lat");
                wallPost.owner.user.address.lng = (this.resultSet.getObject("lng")==null)?0:this.resultSet.getDouble("lng");
                wallPost.owner.user.address.formattedAddress = (this.resultSet.getObject("formatted_address")==null)?"":this.resultSet.getString("formatted_address");
                wallPost.owner.user.address.countryName = (this.resultSet.getObject("country")==null)?"":this.resultSet.getString("country");
                wallPost.owner.user.address.createdDate = (this.resultSet.getObject("locationCDate")==null)?"":this.getProcessedDateTime(this.resultSet.getString("wall_postCdate"));

                wallPost.places.id = (this.resultSet.getObject("postLoc.id")==null)?0:this.resultSet.getInt("postLoc.id");
                wallPost.places.placeId = (this.resultSet.getObject("postLoc.place_id")==null)?"":this.resultSet.getString("postLoc.place_id");
                wallPost.places.icon = (this.resultSet.getObject("postLoc.icon")==null)?"":this.resultSet.getString("postLoc.icon");
                wallPost.places.name = (this.resultSet.getObject("postLoc.name")==null)?"":this.resultSet.getString("postLoc.name");
                wallPost.places.googlePlaceId = (this.resultSet.getObject("postLoc.google_place_id")==null)?"":this.resultSet.getString("postLoc.google_place_id");

                wallPost.places.lat = (this.resultSet.getObject("postLoc.lat")==null)?0:this.resultSet.getDouble("postLoc.lat");
                wallPost.places.lng = (this.resultSet.getObject("postLoc.lng")==null)?0:this.resultSet.getDouble("postLoc.lng");
                wallPost.places.formattedAddress = (this.resultSet.getObject("postLoc.formatted_address")==null)?"":this.resultSet.getString("postLoc.formatted_address");
                wallPost.places.countryName = (this.resultSet.getObject("postLoc.country")==null)?"":this.resultSet.getString("postLoc.country");
                wallPost.places.createdDate = (this.resultSet.getObject("postLoc.created_date")==null)?"":this.resultSet.getString("postLoc.created_date");


                //job details
                wallPost.owner.job.id =(this.resultSet.getObject("job.id")==null)?0:this.resultSet.getInt("job.id");
                wallPost.owner.job.appCredentialId = (this.resultSet.getObject("job.app_login_credential_id")==null)?0:this.resultSet.getInt("job.app_login_credential_id");
                wallPost.owner.job.title = (this.resultSet.getObject("job.title") == null) ? "" : this.resultSet.getString("job.title");
                wallPost.owner.job.description = (this.resultSet.getObject("job.description") == null)? "" : this.resultSet.getString("job.description");
                try{
                    wallPost.owner.job.icons = (this.resultSet.getObject("job.icon")==null || !this.resultSet.getString("job.icon").trim().startsWith("{"))?new Pictures():this.gson.fromJson(this.resultSet.getString("job.icon"),Pictures.class);

                }catch (Exception ex){
                    ex.printStackTrace();
                }
                wallPost.owner.job.price = (this.resultSet.getObject("job.price") == null)?0:this.resultSet.getFloat("job.price");
                wallPost.owner.job.paymentType = (this.resultSet.getObject("job.payment_type") == null)?0:this.resultSet.getInt("job.payment_type");
                try {
                    wallPost.owner.job.createdDate = (this.resultSet.getObject("job.created_date") == null)?"":this.getProcessedDateTime(this.resultSet.getString("job.created_date"));
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                    wallPost.owner.job.createdDate = "";
                }
                //end job details

                wallPost = this.getOtherDependency(wallPost);


                wallPostList.add(wallPost);


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return wallPostList;

    }
    public WallPost getOtherDependency(WallPost wallPost){
        TagListModel tagListModel = new TagListModel();
        tagListModel.setPost_id( wallPost.id);
        wallPost.tagList = tagListModel.getByPostId();
        wallPost.tagCount = wallPost.tagList.size();

        PostCommentModel postCommentModel = new PostCommentModel();
        postCommentModel.setPost_id(wallPost.id);
        wallPost.comments = postCommentModel.getByPostId();
        wallPost.commentCount = wallPost.comments.size();

        PostLikeModel postLikeModel = new PostLikeModel();

        postLikeModel.limit = 5;
        postLikeModel.offset = 0;

        postLikeModel.setPost_id(wallPost.id);
        wallPost.likerList = postLikeModel.getLikersByPostId();
        return  wallPost;
    }
    public boolean isIdExist(){
        String query = "SELECT id FROM wall_post where id = "+this.id+" limit 1";
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
    public int insert() {

        String query = "INSERT INTO `wall_post`(`owner_id`, `description`,`type`, `picture_path`, `location_id`,`wall_post_mood`,`created_date`) " +
                " VALUES (?,?,?,?,?,?,?)";

        try {
            PreparedStatement ps = this.con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setInt((int) 1, this.owner_id);
            ps.setString((int) 2, this.description);
            ps.setInt((int)3, this.type);
            ps.setString((int)4, this.picture_path);
            ps.setInt((int)5, this.location_id);
            ps.setString((int) 6, this.wall_post_mood);
            ps.setString((int)7, this.getUtcDateTime());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next())
            {
                this.id = rs.getInt(1);
            }
            rs.close();
            ps.close();
            //this.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
        }

        return this.id;
    }
    public boolean delete(){
        String query = "DELETE FROM `wall_post` WHERE id = "+this.id;
        if(this.isWallPostOwner()){
            if(this.deleteData(query)==1){
                return true;
            }
            this.errorObj.errStatus = false;
            this.errorObj.msg = "You don't have privilege to delete this wall post";
            return false;
        }
        this.errorObj.errStatus = false;
        this.errorObj.msg = "You don't have privilege to delete this wall post";
        return false;
    }

    /*public int wallpostCountByOwnerId()
    {
        String query = "SELECT count(wall_post.id) FROM `wall_post` WHERE owner_id = "+this.owner_id;

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
    }*/

    public ArrayList<Integer> getWallpostIdByLocationId(String locationIdList) {

        ArrayList<Integer> wallpostIdList = new ArrayList<Integer>();


        String query = "SELECT id FROM wall_post WHERE wall_post.location_id IN ("+locationIdList+")";

        this.setQuery(query);
        this.getData();

        try {
            while (this.resultSet.next()) {
                WallPost wallPost = new WallPost();
                wallPost.id = this.resultSet.getInt("wall_post.id");

                wallpostIdList.add(wallPost.id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return wallpostIdList;



    }

    public boolean updateCommentCount(){

        String updateQuery = "Update wall_post set comment_count=" +
                             "(SELECT count(id) as commentCount FROM post_comment where post_id="+this.getId()+")" +
                             "where wall_post.id="+this.getId();
        this.setQuery(updateQuery);
        if(this.updateData(updateQuery))
        {
            return true;
        }
        return false;

    }
    public boolean updateIsBlockedTrue(WallPost wallPost){

        String updateQuery = "Update wall_post set is_blocked = 1 " +
                "where wall_post.id="+wallPost.id;
        this.setQuery(updateQuery);
        if(this.updateData(updateQuery))
        {
            return true;
        }
        return false;

    }
    public boolean updateIsBlockedFalse(WallPost wallPost){

        String updateQuery = "Update wall_post set is_blocked = 0 " +
                "where wall_post.id="+wallPost.id;
        this.setQuery(updateQuery);
        if(this.updateData(updateQuery))
        {
            return true;
        }
        return false;

    }
}

