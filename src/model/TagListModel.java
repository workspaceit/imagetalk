package model;


import com.google.gson.Gson;
import model.datamodel.app.AppCredential;
import model.datamodel.app.TagList;
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
    public ArrayList<TagList> getByPostId() {

        ArrayList<TagList> tagList = new ArrayList<TagList>();

        String query = "SELECT id,tag_id,post_id,origin_x,origin_y,tag_message from tag_list " +
                       "where post_id=" + this.post_id;
        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                TagList tag = new TagList();

                tag.id = this.resultSet.getInt("id");
                tag.postId = this.resultSet.getInt("post_id");
                tag.originX = this.resultSet.getDouble("origin_x");
                tag.originY = this.resultSet.getDouble("origin_y");
                tag.tagMessage = this.resultSet.getString("tag_message");

                this.setTag_id(this.resultSet.getInt("tag_id"));

                tag = getOtherDependencies(tag);

                tagList.add(tag);

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

    public TagList getOtherDependencies(TagList tag)
    {
        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
        appLoginCredentialModel.setId(this.tag_id);
        tag.tagId = appLoginCredentialModel.getAppCredentialById();
        return tag;
    }
}
