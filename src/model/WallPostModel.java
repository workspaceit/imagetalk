package model;

import com.google.gson.Gson;
import model.datamodel.app.WallPost;
import model.datamodel.photo.Pictures;
import org.apache.commons.lang3.StringEscapeUtils;

import java.security.Timestamp;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mi on 10/2/15.
 */
public class WallPostModel extends ImageTalkBaseModel{


    private int id;
    private int owner_id;
    private String  description;
    private String picture_path;
    private int location_id;
    private String created_date;
    private int currentUserId;

    private Gson gson;
    public WallPostModel(){
        super();
        super.tableName = "wall_post";


        this.id =0;
        this.owner_id=0;
        this.description=null;
        this.picture_path=null;
        this.location_id=0;
        this.created_date="";

        this.currentUserId = 0;
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

    public String getDescrption() {
        return description;
    }

    public boolean setDescrption(String description) {


        this.description = StringEscapeUtils.escapeEcmaScript( description.trim());
        return true;
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

    public int getCurrentUserId() {
        return currentUserId;
    }

    public boolean setCurrentUserId(int currentUserId) {
        this.currentUserId = currentUserId;
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
    public ArrayList<WallPost> getAllRecent(){
        ArrayList<WallPost> wallPostList = new ArrayList<WallPost>();

        String query = "SELECT wall_post.id as wall_post_id,wall_post.owner_id,wall_post.description,wall_post.picture_path,wall_post.location_id,wall_post.created_date as wall_postCdate, " +

                " (select count(id) from post_like where post_like.post_id = wall_post.id ) as likeCount," +
                " (select count(id) from post_comment where post_comment.post_id = wall_post.id ) as commentCount," +
                " (select count(id) from post_like where post_like.post_id = wall_post_id and liker_id = "+this.currentUserId+" limit 1 ) as isLiked," +
                " app_login_credential.id as app_login_credentialId, app_login_credential.text_status, app_login_credential.phone_number, app_login_credential.created_date as app_lCdate," +
                " user_inf.id as user_infId, user_inf.f_name, user_inf.l_name, user_inf.pic_path as proPic, user_inf.address_id, user_inf.created_date as user_infCdate," +
                " location.id as locationId, location.lat, location.lng, location.formatted_address, location.country, location.created_date as locationCDate," +
                " postLoc.*" +
                " FROM wall_post " +
                " join app_login_credential on app_login_credential.id = wall_post.owner_id " +
                " join user_inf on user_inf.id = app_login_credential.u_id " +
                " left join location on location.id = user_inf.address_id " +
                " left join location as postLoc on postLoc.id = wall_post.location_id ";

        query += " order by wall_post_id DESC ";
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
                wallPost.id = this.resultSet.getInt("wall_post_id");
                wallPost.description = this.resultSet.getString("description");
                wallPost.picPath = this.resultSet.getString("wall_post.picture_path");
                wallPost.createdDate = this.getPrcessedTimeStamp(this.resultSet.getTimestamp("wall_postCdate")); //Long.toString(this.resultSet.getTimestamp("wall_postCdate").getTime());
                wallPost.isLiked = (this.resultSet.getInt("isLiked")==1)?true:false;
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

                wallPost.location.id = (this.resultSet.getObject("postLoc.id")==null)?0:this.resultSet.getInt("postLoc.id");
                wallPost.location.lat = (this.resultSet.getObject("postLoc.lat")==null)?0:this.resultSet.getDouble("postLoc.lat");
                wallPost.location.lng = (this.resultSet.getObject("postLoc.lng")==null)?0:this.resultSet.getDouble("postLoc.lng");
                wallPost.location.formattedAddress = (this.resultSet.getObject("postLoc.formatted_address")==null)?"":this.resultSet.getString("postLoc.formatted_address");
                wallPost.location.countryName = (this.resultSet.getObject("postLoc.country")==null)?"":this.resultSet.getString("postLoc.country");
                wallPost.location.createdDate = (this.resultSet.getObject("postLoc.created_date")==null)?"":this.resultSet.getString("postLoc.created_date");


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
        String query = "SELECT wall_post.id as wall_post_id,wall_post.owner_id,wall_post.description,wall_post.picture_path,wall_post.location_id,wall_post.created_date as wall_postCdate, " +

                " (select count(id) from post_like where post_like.post_id = wall_post.id ) as likeCount," +
                " (select count(id) from post_comment where post_comment.post_id = wall_post.id ) as commentCount," +
                " (select count(id) from post_like where post_like.post_id = wall_post_id and liker_id = "+this.currentUserId+" limit 1 ) as isLiked," +
                " app_login_credential.id as app_login_credentialId, app_login_credential.text_status, app_login_credential.phone_number, app_login_credential.created_date as app_lCdate," +
                " user_inf.id as user_infId, user_inf.f_name, user_inf.l_name, user_inf.pic_path as proPic, user_inf.address_id, user_inf.created_date as user_infCdate," +
                " location.id as locationId, location.lat, location.lng, location.formatted_address, location.country, location.created_date as locationCDate," +
                " postLoc.*" +
                " FROM wall_post " +
                " join app_login_credential on app_login_credential.id = wall_post.owner_id " +
                " join user_inf on user_inf.id = app_login_credential.u_id " +
                " left join location on location.id = user_inf.address_id " +
                " left join location as postLoc on postLoc.id = wall_post.location_id " +
                " where wall_post.id = "+this.id+" limit 1";



        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                wallPost.id = this.resultSet.getInt("wall_post_id");
                wallPost.description = this.resultSet.getString("description");
                wallPost.picPath = this.resultSet.getString("wall_post.picture_path");
                wallPost.likeCount = this.resultSet.getInt("likeCount");
                wallPost.commentCount = this.resultSet.getInt("commentCount");

                wallPost.createdDate = Long.toString(this.resultSet.getTimestamp("wall_postCdate").getTime());
                wallPost.isLiked = (this.resultSet.getInt("isLiked")==1)?true:false;

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

                wallPost.location.id = (this.resultSet.getObject("postLoc.id")==null)?0:this.resultSet.getInt("postLoc.id");
                wallPost.location.lat = (this.resultSet.getObject("postLoc.lat")==null)?0:this.resultSet.getDouble("postLoc.lat");
                wallPost.location.lng = (this.resultSet.getObject("postLoc.lng")==null)?0:this.resultSet.getDouble("postLoc.lng");
                wallPost.location.formattedAddress = (this.resultSet.getObject("postLoc.formatted_address")==null)?"":this.resultSet.getString("postLoc.formatted_address");
                wallPost.location.countryName = (this.resultSet.getObject("postLoc.country")==null)?"":this.resultSet.getString("postLoc.country");
                wallPost.location.createdDate = (this.resultSet.getObject("postLoc.created_date")==null)?"":this.resultSet.getString("postLoc.created_date");

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

        String query = "SELECT wall_post.id as wall_post_id,wall_post.owner_id,wall_post.description,wall_post.picture_path,wall_post.location_id,wall_post.created_date as wall_postCdate, " +

                " (select count(id) from post_like where post_like.post_id = wall_post.id ) as likeCount," +
                " (select count(id) from post_comment where post_comment.post_id = wall_post.id ) as commentCount," +
                " (select count(id) from post_like where post_like.post_id = wall_post_id and liker_id = "+this.currentUserId+" limit 1 ) as isLiked," +
                " app_login_credential.id as app_login_credentialId, app_login_credential.text_status, app_login_credential.phone_number, app_login_credential.created_date as app_lCdate," +
                " user_inf.id as user_infId, user_inf.f_name, user_inf.l_name, user_inf.pic_path as proPic, user_inf.address_id, user_inf.created_date as user_infCdate," +
                " location.id as locationId, location.lat, location.lng, location.formatted_address, location.country, location.created_date as locationCDate," +
                " postLoc.*" +
                " FROM wall_post " +
                " join app_login_credential on app_login_credential.id = wall_post.owner_id " +
                " join user_inf on user_inf.id = app_login_credential.u_id " +
                " left join location on location.id = user_inf.address_id " +
                " left join location as postLoc on postLoc.id = wall_post.location_id " +
                " where wall_post.owner_id = "+this.owner_id;


        if(this.limit >0){
            this.offset = this.offset * this.limit;
            query += " LIMIT "+this.offset+" ,"+this.limit+" ";
        }

        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                WallPost wallPost = new WallPost();
                wallPost.id = this.resultSet.getInt("wall_post_id");
                wallPost.description = this.resultSet.getString("description");
                wallPost.picPath = this.resultSet.getString("wall_post.picture_path");
                wallPost.isLiked = (this.resultSet.getInt("isLiked")==1)?true:false;
                wallPost.likeCount = this.resultSet.getInt("likeCount");
                wallPost.commentCount = this.resultSet.getInt("commentCount");

                System.out.println(this.resultSet.getTimestamp("wall_postCdate").getTime());
                System.out.println(this.resultSet.getTimestamp("wall_postCdate"));

                wallPost.createdDate = Long.toString(this.resultSet.getTimestamp("wall_postCdate").getTime());

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
                wallPost.owner.user.address.createdDate = (this.resultSet.getObject("locationCDate")==null)?"":Long.toString(this.resultSet.getTimestamp("wall_postCdate").getTime());

                wallPost.location.id = (this.resultSet.getObject("postLoc.id")==null)?0:this.resultSet.getInt("postLoc.id");
                wallPost.location.lat = (this.resultSet.getObject("postLoc.lat")==null)?0:this.resultSet.getDouble("postLoc.lat");
                wallPost.location.lng = (this.resultSet.getObject("postLoc.lng")==null)?0:this.resultSet.getDouble("postLoc.lng");
                wallPost.location.formattedAddress = (this.resultSet.getObject("postLoc.formatted_address")==null)?"":this.resultSet.getString("postLoc.formatted_address");
                wallPost.location.countryName = (this.resultSet.getObject("postLoc.country")==null)?"":this.resultSet.getString("postLoc.country");
                wallPost.location.createdDate = (this.resultSet.getObject("postLoc.created_date")==null)?"0":Long.toString(this.resultSet.getTimestamp("wall_postCdate").getTime());


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

        String query = "INSERT INTO `wall_post`(`owner_id`, `description`, `picture_path`, `location_id`,`created_date`) " +
                " VALUES (" + this.owner_id + ",'" + this.description + "','" + this.picture_path + "'," + this.location_id +",'"+ this.getUtcDateTime() + "')";

        this.id = this.insertData(query);
        return this.id;
    }
}

