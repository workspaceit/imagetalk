package model;

import model.datamodel.app.AppCredential;
import model.datamodel.photo.Pictures;

import java.sql.SQLException;

/**
 * Created by mi on 10/7/15.
 */
public class PostLikeModel extends  ImageTalkBaseModel {

    private int id;
    private int liker_id;
    private int post_id;
    private String created_date;
    public PostLikeModel() {
        super();
        super.tableName = "post_like";
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
}
