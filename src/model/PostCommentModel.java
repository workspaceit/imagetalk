package model;

import com.google.gson.Gson;
import model.ImageTalkBaseModel;
import model.TagListModel;
import model.datamodel.app.PostComment;
import model.datamodel.app.PostCommentReply;
import model.datamodel.app.WallPost;
import model.datamodel.photo.Pictures;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by mi on 10/5/15.
 */
public class PostCommentModel extends ImageTalkBaseModel {

    private int id;
    private String comment;
    private String pic_path;
    private int post_id;
    private int  commenter_id;
    private int parent_id;
    private int last_reply_id;
    private String created_date;

    private Gson gson;
    public PostCommentModel() {
        super();
        super.tableName = "post_comment";


        this.id = 0;
        this.comment = "";
        this.pic_path = "";
        this.post_id = 0;
        this.last_reply_id = 0;
        this.commenter_id=0;
        this.parent_id = 0;
        this.created_date = "";

        this.gson = new Gson();
    }

    public int getId() {
        return id;
    }

    public boolean setLastReplyId(int last_reply_id) {
        this.last_reply_id = last_reply_id;
        return true;
    }
    public int getLastReplyId() {
        return last_reply_id;
    }

    public boolean setId(int id) {
        this.id = id;
        return true;
    }


    public String getComment() {
        return comment;
    }

    public boolean setComment(String comment) {
        try {
            this.comment =   new String(comment.getBytes("UTF-8"),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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

    public int getParentId() {
        return parent_id;

    }

    public boolean setParentId(int parent_id) {
        this.parent_id = parent_id;
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
        String query = "INSERT INTO "+this.tableName+" (comment,pic_path, post_id, commenter_id,created_date)";
        query +="VALUES ('"+this.comment+"','"+this.pic_path+"',"+this.post_id+","+this.commenter_id+",'"+this.getUtcDateTime()+"')";


        String query1 = "INSERT INTO "+this.tableName+" (`comment`, `pic_path`,`post_id`, `commenter_id`, `created_date`) " +
                " VALUES (?,?,?,?,?)";

        PreparedStatement ps = null;
        try {
            ps = this.con.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
            ps.setString((int) 1, this.comment);
            ps.setString((int) 2, this.pic_path);
            ps.setInt((int) 3, this.post_id);
            ps.setInt((int) 4, this.commenter_id);
            ps.setString((int)5, this.getUtcDateTime());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next())
            {
                this.id = rs.getInt(1);
            }
            rs.close();
            ps.close();
            this.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        //this.id = this.insertData(query);
        return this.id;
    }
    public int insertCommentReply(){
        String query = "INSERT INTO "+this.tableName+" (comment,pic_path, post_id, commenter_id,parent_id,created_date)";
        query +="VALUES ('"+this.comment+"','"+this.pic_path+"',"+this.post_id+","+this.commenter_id+",'"+this.getUtcDateTime()+"')";


        String query1 = "INSERT INTO "+this.tableName+" (`comment`, `pic_path`,`post_id`, `commenter_id`,`parent_id`, `created_date`) " +
                        " VALUES (?,?,?,?,?,?)";

        PreparedStatement ps = null;
        try {
            ps = this.con.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
            ps.setString((int) 1, this.comment);
            ps.setString((int) 2, this.pic_path);
            ps.setInt((int) 3, this.post_id);
            ps.setInt((int) 4, this.commenter_id);
            ps.setInt((int) 5, this.parent_id);
            ps.setString((int)6, this.getUtcDateTime());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next())
            {
                this.id = rs.getInt(1);
            }
            rs.close();
            ps.close();
            this.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        //this.id = this.insertData(query);
        return this.id;
    }

    public  ArrayList<PostComment> getByPostId(){
        ArrayList<PostComment> postCommentList = new ArrayList<PostComment>();
        String query = "SELECT " +this.tableName+".id as postCommentId,"+this.tableName+".comment,"+this.tableName+".pic_path as commentPicPath,"+this.tableName+".created_date as postCommentCDate,"+
                " app_login_credential.id as app_login_credentialId, app_login_credential.text_status, app_login_credential.phone_number, app_login_credential.created_date as app_lCdate," +
                " user_inf.id as user_infId, user_inf.f_name, user_inf.l_name, user_inf.pic_path as proPic, user_inf.address_id, user_inf.created_date as user_infCdate," +
                " location.id as locationId, location.lat, location.lng, location.formatted_address, location.country, location.created_date as locationCDate,job.*" +
                " FROM " +this.tableName+
                " join app_login_credential on app_login_credential.id =  " +this.tableName+".commenter_id "+
                " join user_inf on user_inf.id = app_login_credential.u_id " +
                " left join location on location.id = user_inf.address_id " +
                " left join job on job.app_login_credential_id = app_login_credential.id " +
                " where "+this.tableName+".post_id = "+this.post_id+" ";
        if(this.limit >0){
            this.offset = this.offset * this.limit;
            query += " LIMIT "+this.offset+" ,"+this.limit+" ";
        }

        //System.out.println("query of post comment ::" + query);

        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                PostComment postComment = new PostComment();
                postComment.id = this.resultSet.getInt("postCommentId");
                postComment.comment = this.resultSet.getString("comment");
                postComment.picPath = this.resultSet.getString("commentPicPath");
                postComment.createdDate = Long.toString(this.resultSet.getTimestamp("postCommentCDate").getTime());

                postComment.commenter.id = this.resultSet.getInt("app_login_credentialId");
                postComment.commenter.textStatus = this.resultSet.getString("text_status");
                postComment.commenter.phoneNumber = this.resultSet.getString("phone_number");
                postComment.commenter.createdDate = this.resultSet.getString("app_lCdate");

                postComment.commenter.user.id = this.resultSet.getInt("user_infId");
                postComment.commenter.user.firstName = this.resultSet.getString("f_name");
                postComment.commenter.user.lastName = this.resultSet.getString("l_name");

                try{
                    postComment.commenter.user.picPath  = this.gson.fromJson(this.resultSet.getString("proPic"), Pictures.class);
                }catch (Exception ex){
                    postComment.commenter.user.picPath.original.path  = this.resultSet.getString("proPic");

                    ex.printStackTrace();
                }
                postComment.commenter.user.address.id = (this.resultSet.getObject("locationId")==null)?0:this.resultSet.getInt("locationId");
                postComment.commenter.user.address.lat = (this.resultSet.getObject("lat")==null)?0:this.resultSet.getDouble("lat");
                postComment.commenter.user.address.lng = (this.resultSet.getObject("lng")==null)?0:this.resultSet.getDouble("lng");
                postComment.commenter.user.address.formattedAddress = (this.resultSet.getObject("formatted_address")==null)?"":this.resultSet.getString("formatted_address");
                postComment.commenter.user.address.countryName = (this.resultSet.getObject("country")==null)?"":this.resultSet.getString("country");
                postComment.commenter.user.address.createdDate = (this.resultSet.getObject("locationCDate")==null)?"":this.resultSet.getString("locationCDate");

                //job details
                postComment.commenter.job.id =(this.resultSet.getObject("job.id")==null)?0:this.resultSet.getInt("job.id");
                postComment.commenter.job.appCredentialId = (this.resultSet.getObject("job.app_login_credential_id")==null)?0:this.resultSet.getInt("job.app_login_credential_id");
                postComment.commenter.job.title = (this.resultSet.getObject("job.title") == null) ? "" : this.resultSet.getString("job.title");
                postComment.commenter.job.description = (this.resultSet.getObject("job.description") == null)? "" : this.resultSet.getString("job.description");
                try{
                    postComment.commenter.job.icons = (this.resultSet.getObject("job.icon")==null || !this.resultSet.getString("job.icon").trim().startsWith("{"))?new Pictures():this.gson.fromJson(this.resultSet.getString("job.icon"),Pictures.class);

                }catch (Exception ex){
                    ex.printStackTrace();
                }
                postComment.commenter.job.price = (this.resultSet.getObject("job.price") == null)?0:this.resultSet.getFloat("job.price");
                postComment.commenter.job.paymentType = (this.resultSet.getObject("job.payment_type") == null)?0:this.resultSet.getInt("job.payment_type");
                try {
                    postComment.commenter.job.createdDate = (this.resultSet.getObject("job.created_date") == null)?"":this.getPrcessedTimeStamp(this.resultSet.getTimestamp("job.created_date"));
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                    postComment.commenter.job.createdDate = "";
                }
                //end job details
                PostCommentModel postCommentModel = new PostCommentModel();
                postCommentModel.setParentId(postComment.id);
                postComment = this.getOtherDependency(postComment);

                postCommentList.add(postComment);


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return postCommentList;
    }
    public  PostComment getById(){
        PostComment postComment = new PostComment();
        String query = "SELECT " +this.tableName+".id as postCommentId,"+this.tableName+".comment,"+this.tableName+".pic_path as commentPicPath,"+this.tableName+".created_date as postCommentCDate,"+
                " app_login_credential.id as app_login_credentialId, app_login_credential.text_status, app_login_credential.phone_number, app_login_credential.created_date as app_lCdate," +
                " user_inf.id as user_infId, user_inf.f_name, user_inf.l_name, user_inf.pic_path as proPic, user_inf.address_id, user_inf.created_date as user_infCdate," +
                " location.id as locationId, location.lat, location.lng, location.formatted_address, location.country, location.created_date as locationCDate,job.*" +
                " FROM " +this.tableName+
                " join app_login_credential on app_login_credential.id =  " +this.tableName+".commenter_id "+
                " join user_inf on user_inf.id = app_login_credential.u_id " +
                " left join location on location.id = user_inf.address_id " +
                " left join job on job.app_login_credential_id = app_login_credential.id " +
                " where "+this.tableName+".id = "+this.id+" limit 1";



        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {

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
                postComment.commenter.user.createdDate = this.resultSet.getString("user_infCdate");

                try{
                    postComment.commenter.user.picPath  = this.gson.fromJson(this.resultSet.getString("proPic"), Pictures.class);
                }catch (Exception ex){
                    postComment.commenter.user.picPath.original.path  = this.resultSet.getString("proPic");

                    ex.printStackTrace();
                }
                postComment.commenter.user.address.id = (this.resultSet.getObject("locationId")==null)?0:this.resultSet.getInt("locationId");
                postComment.commenter.user.address.lat = (this.resultSet.getObject("lat")==null)?0:this.resultSet.getDouble("lat");
                postComment.commenter.user.address.lng = (this.resultSet.getObject("lng")==null)?0:this.resultSet.getDouble("lng");
                postComment.commenter.user.address.formattedAddress = (this.resultSet.getObject("formatted_address")==null)?"":this.resultSet.getString("formatted_address");
                postComment.commenter.user.address.countryName = (this.resultSet.getObject("country")==null)?"":this.resultSet.getString("country");
                postComment.commenter.user.address.createdDate = (this.resultSet.getObject("locationCDate")==null)?"":this.resultSet.getString("locationCDate");

                //job details
                postComment.commenter.job.id =(this.resultSet.getObject("job.id")==null)?0:this.resultSet.getInt("job.id");
                postComment.commenter.job.appCredentialId = (this.resultSet.getObject("job.app_login_credential_id")==null)?0:this.resultSet.getInt("job.app_login_credential_id");
                postComment.commenter.job.title = (this.resultSet.getObject("job.title") == null) ? "" : this.resultSet.getString("job.title");
                postComment.commenter.job.description = (this.resultSet.getObject("job.description") == null)? "" : this.resultSet.getString("job.description");
                try{
                    postComment.commenter.job.icons = (this.resultSet.getObject("job.icon")==null || !this.resultSet.getString("job.icon").trim().startsWith("{"))?new Pictures():this.gson.fromJson(this.resultSet.getString("job.icon"), Pictures.class);

                }catch (Exception ex){
                    ex.printStackTrace();
                }
                postComment.commenter.job.price = (this.resultSet.getObject("job.price") == null)?0:this.resultSet.getFloat("job.price");
                postComment.commenter.job.paymentType = (this.resultSet.getObject("job.payment_type") == null)?0:this.resultSet.getInt("job.payment_type");
                try {
                    postComment.commenter.job.createdDate = (this.resultSet.getObject("job.created_date") == null)?"":this.getPrcessedTimeStamp(this.resultSet.getTimestamp("job.created_date"));
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                    postComment.commenter.job.createdDate = "";
                }
                //end job details

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return postComment;
    }
    public  int getPostIdById(){
        String query = "SELECT post_id FROM " +this.tableName+
                " where "+this.tableName+".id = "+this.id+" limit 1";

        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                return this.resultSet.getInt("post_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return 0;
    }
    public  int getCountByPostId(){
        String query = "SELECT count(id) as commentCount FROM " +this.tableName+
                " where "+this.tableName+".post_id = "+this.post_id+" ";

        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                return this.resultSet.getInt("commentCount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return 0;
    }
    public  boolean isCommenter(){
        String query = "SELECT id FROM " +this.tableName+
                " where "+this.tableName+".id = "+this.id+" and commenter_id = "+this.commenter_id+" limit 1";
        System.out.println(query);
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
    public  int deleteById(){
        String query = "delete from " +this.tableName+
                " where "+this.tableName+".id = "+this.id+" limit 1";

        return this.deleteData(query);
    }

    public ArrayList<PostCommentReply> getPostCommentReplyByParentId(){

        ArrayList<PostCommentReply> postCommentRepliesList = new ArrayList<PostCommentReply>();
        String query = "SELECT " +this.tableName+".id as postCommentId,"+this.tableName+".comment,"+this.tableName+".pic_path as commentPicPath,"+this.tableName+".parent_id as parentId,"+this.tableName+".created_date as postCommentCDate,"+
                       " app_login_credential.id as app_login_credentialId, app_login_credential.text_status, app_login_credential.phone_number, app_login_credential.created_date as app_lCdate," +
                       " user_inf.id as user_infId, user_inf.f_name, user_inf.l_name, user_inf.pic_path as proPic, user_inf.address_id, user_inf.created_date as user_infCdate," +
                       " location.id as locationId, location.lat, location.lng, location.formatted_address, location.country, location.created_date as locationCDate,job.*" +
                       " FROM " +this.tableName+
                       " join app_login_credential on app_login_credential.id =  " +this.tableName+".commenter_id "+
                       " join user_inf on user_inf.id = app_login_credential.u_id " +
                       " left join location on location.id = user_inf.address_id " +
                       " left join job on job.app_login_credential_id = app_login_credential.id " +
                       " where "+this.tableName+".parent_id = "+this.parent_id+" AND "+this.tableName+".id >"+this.last_reply_id;
        //System.out.println("parent id in comment reply :" + this.parent_id);
        //if(this.limit >0){
            this.offset = this.offset * this.limit;
            query += " LIMIT "+this.offset+" ,"+this.limit+" ";
        //}

        System.out.println("Comment Reply query ::"+query);
        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                PostCommentReply postCommentReply = new PostCommentReply();
                postCommentReply.id = this.resultSet.getInt("postCommentId");
                postCommentReply.comment = this.resultSet.getString("comment");
                postCommentReply.picPath = this.resultSet.getString("commentPicPath");
                postCommentReply.parentId = this.resultSet.getInt("parentId");
                postCommentReply.createdDate = Long.toString(this.resultSet.getTimestamp("postCommentCDate").getTime());

                postCommentReply.commenter.id = this.resultSet.getInt("app_login_credentialId");
                postCommentReply.commenter.textStatus = this.resultSet.getString("text_status");
                postCommentReply.commenter.phoneNumber = this.resultSet.getString("phone_number");
                postCommentReply.commenter.createdDate = this.resultSet.getString("app_lCdate");

                postCommentReply.commenter.user.id = this.resultSet.getInt("user_infId");
                postCommentReply.commenter.user.firstName = this.resultSet.getString("f_name");
                postCommentReply.commenter.user.lastName = this.resultSet.getString("l_name");

                try{
                    postCommentReply.commenter.user.picPath  = this.gson.fromJson(this.resultSet.getString("proPic"), Pictures.class);
                }catch (Exception ex){
                    postCommentReply.commenter.user.picPath.original.path  = this.resultSet.getString("proPic");

                    ex.printStackTrace();
                }
                postCommentReply.commenter.user.address.id = (this.resultSet.getObject("locationId")==null)?0:this.resultSet.getInt("locationId");
                postCommentReply.commenter.user.address.lat = (this.resultSet.getObject("lat")==null)?0:this.resultSet.getDouble("lat");
                postCommentReply.commenter.user.address.lng = (this.resultSet.getObject("lng")==null)?0:this.resultSet.getDouble("lng");
                postCommentReply.commenter.user.address.formattedAddress = (this.resultSet.getObject("formatted_address")==null)?"":this.resultSet.getString("formatted_address");
                postCommentReply.commenter.user.address.countryName = (this.resultSet.getObject("country")==null)?"":this.resultSet.getString("country");
                postCommentReply.commenter.user.address.createdDate = (this.resultSet.getObject("locationCDate")==null)?"":this.resultSet.getString("locationCDate");

                //job details
                postCommentReply.commenter.job.id =(this.resultSet.getObject("job.id")==null)?0:this.resultSet.getInt("job.id");
                postCommentReply.commenter.job.appCredentialId = (this.resultSet.getObject("job.app_login_credential_id")==null)?0:this.resultSet.getInt("job.app_login_credential_id");
                postCommentReply.commenter.job.title = (this.resultSet.getObject("job.title") == null) ? "" : this.resultSet.getString("job.title");
                postCommentReply.commenter.job.description = (this.resultSet.getObject("job.description") == null)? "" : this.resultSet.getString("job.description");
                try{
                    postCommentReply.commenter.job.icons = (this.resultSet.getObject("job.icon")==null || !this.resultSet.getString("job.icon").trim().startsWith("{"))?new Pictures():this.gson.fromJson(this.resultSet.getString("job.icon"),Pictures.class);

                }catch (Exception ex){
                    ex.printStackTrace();
                }
                postCommentReply.commenter.job.price = (this.resultSet.getObject("job.price") == null)?0:this.resultSet.getFloat("job.price");
                postCommentReply.commenter.job.paymentType = (this.resultSet.getObject("job.payment_type") == null)?0:this.resultSet.getInt("job.payment_type");
                try {
                    postCommentReply.commenter.job.createdDate = (this.resultSet.getObject("job.created_date") == null)?"":this.getPrcessedTimeStamp(this.resultSet.getTimestamp("job.created_date"));
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                    postCommentReply.commenter.job.createdDate = "";
                }
                //end job details
                postCommentRepliesList.add(postCommentReply);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return postCommentRepliesList;
    }

    public PostComment getOtherDependency(PostComment postComment){

        PostCommentModel postCommentModel = new PostCommentModel();
        postCommentModel.setParentId(postComment.id);
        postCommentModel.offset = 0;
        postCommentModel.limit = 3;

        //System.out.println("other dependency :" + postCommentModel.offset);

        postComment.commentReplies = postCommentModel.getPostCommentReplyByParentId();
        return postComment;

    }

}
