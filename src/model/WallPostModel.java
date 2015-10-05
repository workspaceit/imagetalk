package model;

import model.datamodel.PostCommentModel;
import model.datamodel.app.PostComment;
import model.datamodel.app.WallPost;
import org.apache.commons.lang3.StringEscapeUtils;

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

    public WallPostModel(){
        super();
        super.tableName = "wall_post";


        this.id =0;
        this.owner_id=0;
        this.description=null;
        this.picture_path=null;
        this.location_id=0;
        this.created_date="";

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
    public WallPost getById(){
        WallPost wallPost = new WallPost();
        String query = "SELECT wall_post.id as wall_post_id,wall_post.owner_id,wall_post.description,wall_post.picture_path,wall_post.location_id,wall_post.created_date as wall_postCdate, " +

                " (select count(id) from post_like where post_like.post_id = wall_post.id ) as likeCount," +
                " app_login_credential.id as app_login_credentialId, app_login_credential.text_status, app_login_credential.phone_number, app_login_credential.created_date as app_lCdate," +
                " user_inf.id as user_infId, user_inf.f_name, user_inf.l_name, user_inf.pic_path as proPic, user_inf.address_id, user_inf.created_date as user_infCdate," +
                " location.id as locationId, location.lat, location.lon, location.formatted_address, location.country, location.created_date as locationCDate" +
                " FROM wall_post " +
                " join app_login_credential on app_login_credential.id = wall_post.owner_id " +
                " join user_inf on user_inf.id = app_login_credential.u_id " +
                " left join location on location.id = user_inf.address_id " +
                " where wall_post.id = "+this.id+" limit 1";


        System.out.println(query);
        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                wallPost.id = this.resultSet.getInt("wall_post_id");
                wallPost.description = this.resultSet.getString("description");
                wallPost.picPath = this.resultSet.getString("proPic");
                wallPost.likeCount = this.resultSet.getInt("likeCount");
                wallPost.createdDate = this.resultSet.getString("wall_postCdate");


                wallPost.owner.id = this.resultSet.getInt("app_login_credentialId");
                wallPost.owner.textStatus = this.resultSet.getString("text_status");
                wallPost.owner.phoneNumber = this.resultSet.getString("phone_number");
                wallPost.owner.createdDate = this.resultSet.getString("app_lCdate");

                wallPost.owner.user.id = this.resultSet.getInt("user_infId");
                wallPost.owner.user.firstName = this.resultSet.getString("f_name");
                wallPost.owner.user.lastName = this.resultSet.getString("l_name");
                wallPost.owner.user.picPath = this.resultSet.getString("proPic");


                wallPost.owner.user.address.id = (this.resultSet.getObject("locationId")==null)?0:this.resultSet.getInt("locationId");
                wallPost.owner.user.address.lat = (this.resultSet.getObject("lat")==null)?0:this.resultSet.getDouble("lat");
                wallPost.owner.user.address.lng = (this.resultSet.getObject("lon")==null)?0:this.resultSet.getDouble("lon");
                wallPost.owner.user.address.formattedAddress = (this.resultSet.getObject("formatted_address")==null)?"":this.resultSet.getString("formatted_address");
                wallPost.owner.user.address.countryName = (this.resultSet.getObject("country")==null)?"":this.resultSet.getString("country");
                wallPost.owner.user.address.createdDate = (this.resultSet.getObject("locationCDate")==null)?"":this.resultSet.getString("locationCDate");

                TagListModel tagListModel = new TagListModel();
                tagListModel.setPost_id( wallPost.id);
                wallPost.taglist = tagListModel.getByPostId();
                wallPost.tagCount = wallPost.taglist.size();

                PostCommentModel postCommentModel = new PostCommentModel();
                postCommentModel.setPost_id(wallPost.id);
                wallPost.comments = postCommentModel.getByPostId();
                wallPost.commentCount = wallPost.comments.size();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return wallPost;

    }
    public ArrayList<WallPost> getByOwner_id(){
        ArrayList<WallPost> wallPostList = new ArrayList<WallPost>();

        String query = "SELECT wall_post.id as wall_post_id,wall_post.owner_id,wall_post.description,wall_post.picture_path,wall_post.location_id,wall_post.created_date as wall_postCdate, " +

                " (select count(id) from post_like where post_like.post_id = wall_post_id ) as likeCount," +
                " app_login_credential.id as app_login_credentialId, app_login_credential.text_status, app_login_credential.phone_number, app_login_credential.created_date as app_lCdate," +
                " user_inf.id as user_infId, user_inf.f_name, user_inf.l_name, user_inf.pic_path as proPic, user_inf.address_id, user_inf.created_date as user_infCdate," +
                " location.id as locationId, location.lat, location.lon, location.formatted_address, location.country, location.created_date as locationCDate" +
                " FROM wall_post " +
                " join app_login_credential on app_login_credential.id = wall_post.owner_id " +
                " join user_inf on user_inf.id = app_login_credential.u_id " +
                " left join location on location.id = user_inf.address_id " +
                " where wall_post.owner_id = "+this.owner_id;


        System.out.println(query);
        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                WallPost wallPost = new WallPost();
                wallPost.id = this.resultSet.getInt("wall_post_id");
                wallPost.description = this.resultSet.getString("description");
                wallPost.picPath = this.resultSet.getString("proPic");
                wallPost.createdDate = this.resultSet.getString("wall_postCdate");

                wallPost.owner.id = this.resultSet.getInt("app_login_credentialId");
                wallPost.owner.textStatus = this.resultSet.getString("text_status");
                wallPost.owner.phoneNumber = this.resultSet.getString("phone_number");
                wallPost.owner.createdDate = this.resultSet.getString("app_lCdate");

                wallPost.owner.user.id = this.resultSet.getInt("user_infId");
                wallPost.owner.user.firstName = this.resultSet.getString("f_name");
                wallPost.owner.user.lastName = this.resultSet.getString("l_name");
                wallPost.owner.user.picPath = this.resultSet.getString("proPic");


                wallPost.owner.user.address.id = (this.resultSet.getObject("locationId")==null)?0:this.resultSet.getInt("locationId");
                wallPost.owner.user.address.lat = (this.resultSet.getObject("lat")==null)?0:this.resultSet.getDouble("lat");
                wallPost.owner.user.address.lng = (this.resultSet.getObject("lon")==null)?0:this.resultSet.getDouble("lon");
                wallPost.owner.user.address.formattedAddress = (this.resultSet.getObject("formatted_address")==null)?"":this.resultSet.getString("formatted_address");
                wallPost.owner.user.address.countryName = (this.resultSet.getObject("country")==null)?"":this.resultSet.getString("country");
                wallPost.owner.user.address.createdDate = (this.resultSet.getObject("locationCDate")==null)?"":this.resultSet.getString("locationCDate");

                TagListModel tagListModel = new TagListModel();
                tagListModel.setPost_id( wallPost.id);
                wallPost.taglist = tagListModel.getByPostId();
                wallPost.tagCount = wallPost.taglist.size();

                PostCommentModel postCommentModel = new PostCommentModel();
                postCommentModel.setPost_id(wallPost.id);
                wallPost.comments = postCommentModel.getByPostId();
                wallPost.commentCount = wallPost.comments.size();

                wallPostList.add(wallPost);


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return wallPostList;

    }
    public int insert() {

        String query = "INSERT INTO `wall_post`(`owner_id`, `description`, `picture_path`, `location_id`) " +
                " VALUES (" + this.owner_id + ",'" + this.description + "','" + this.picture_path + "'," + this.location_id + ")";

        this.id = this.insertData(query);
        return this.id;
    }
}

