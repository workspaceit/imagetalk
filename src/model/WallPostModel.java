package model;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Created by mi on 10/2/15.
 */
public class WallPostModel extends ImageTalkBaseModel{


    public int id;
    public int owner_id;
    public String  description;
    public String picture_path;
    public int location_id;
    public String created_date;

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

    public int insert() {

        String query = "INSERT INTO `wall_post`(`owner_id`, `description`, `picture_path`, `location_id`) " +
                " VALUES (" + this.owner_id + ",'" + this.description + "','" + this.picture_path + "'," + this.location_id + ")";

        System.out.println(query);
        return this.insertData(query);
    }
}

