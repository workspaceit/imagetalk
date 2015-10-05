package model.datamodel;

import model.ImageTalkBaseModel;
import model.TagListModel;
import model.datamodel.app.PostComment;
import model.datamodel.app.WallPost;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mi on 10/5/15.
 */
public class PostCommentModel extends ImageTalkBaseModel {

    public int id;
    public String comment;
    public String pic_path;
    public int post_id;
    public int  commenter_id;
    public String created_date;
    public PostCommentModel() {
        super();
        super.tableName = "post_comment";


        this.id = 0;
        this.comment = "";
        this.pic_path = "";
        this.post_id = 0;
        this.commenter_id=0;
        this.created_date = "";
    }

    public int getId() {
        return id;
    }

    public boolean setId(int id) {
        this.id = id;
        return true;
    }

    public String getComment() {
        return comment;
    }

    public boolean setComment(String comment) {
        this.comment = comment;
        return true;
    }

    public String getPic_path() {
        return pic_path;
    }

    public boolean setPic_path(String pic_path) {
        this.pic_path = pic_path;
        return true;
    }

    public int getPost_id() {
        return post_id;
    }

    public boolean setPost_id(int post_id) {
        this.post_id = post_id;
        return true;
    }

    public int getCommenter_id() {
        return commenter_id;

    }

    public boolean setCommenter_id(int commenter_id) {
        this.commenter_id = commenter_id;
        return true;
    }

    public String getCreated_date() {
        return created_date;
    }

    public boolean setCreated_date(String created_date) {
        this.created_date = created_date;
        return true;
    }
    public  ArrayList<PostComment> getByPostId(){
        ArrayList<PostComment> postCommentList = new ArrayList<PostComment>();
        String query = "SELECT " +this.tableName+".id as postCommentId,"+this.tableName+".comment,"+this.tableName+".pic_path as commentPicPath,"+this.tableName+".created_date as postCommentCDate,"+
                " app_login_credential.id as app_login_credentialId, app_login_credential.text_status, app_login_credential.phone_number, app_login_credential.created_date as app_lCdate," +
                " user_inf.id as user_infId, user_inf.f_name, user_inf.l_name, user_inf.pic_path as proPic, user_inf.address_id, user_inf.created_date as user_infCdate," +
                " location.id as locationId, location.lat, location.lon, location.formatted_address, location.country, location.created_date as locationCDate" +
                " FROM " +this.tableName+
                " join app_login_credential on app_login_credential.id =  " +this.tableName+".commenter_id "+
                " join user_inf on user_inf.id = app_login_credential.u_id " +
                " left join location on location.id = user_inf.address_id " +
                " where "+this.tableName+".post_id = "+this.post_id+" ";


        System.out.println(query);
        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                PostComment postComment = new PostComment();
                postComment.id = this.resultSet.getInt("postCommentId");
                postComment.comment = this.resultSet.getString("comment");
                postComment.picPath = this.resultSet.getString("commentPicPath");
                postComment.createdDate = this.resultSet.getString("postCommentCDate");

                postComment.commenter.id = this.resultSet.getInt("app_login_credentialId");
                postComment.commenter.textStatus = this.resultSet.getString("text_status");
                postComment.commenter.phoneNumber = this.resultSet.getString("phone_number");
                postComment.commenter.createdDate = this.resultSet.getString("app_lCdate");

                postComment.commenter.user.id = this.resultSet.getInt("user_infId");
                postComment.commenter.user.firstName = this.resultSet.getString("f_name");
                postComment.commenter.user.lastName = this.resultSet.getString("l_name");
                postComment.commenter.user.picPath = this.resultSet.getString("proPic");


                postComment.commenter.user.address.id = (this.resultSet.getObject("locationId")==null)?0:this.resultSet.getInt("locationId");
                postComment.commenter.user.address.lat = (this.resultSet.getObject("lat")==null)?0:this.resultSet.getDouble("lat");
                postComment.commenter.user.address.lng = (this.resultSet.getObject("lon")==null)?0:this.resultSet.getDouble("lon");
                postComment.commenter.user.address.formattedAddress = (this.resultSet.getObject("formatted_address")==null)?"":this.resultSet.getString("formatted_address");
                postComment.commenter.user.address.countryName = (this.resultSet.getObject("country")==null)?"":this.resultSet.getString("country");
                postComment.commenter.user.address.createdDate = (this.resultSet.getObject("locationCDate")==null)?"":this.resultSet.getString("locationCDate");

                postCommentList.add(postComment);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return postCommentList;
    }

}
