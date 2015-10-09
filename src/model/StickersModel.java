package model;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Created by mi on 10/9/15.
 */
public class StickersModel extends ImageTalkBaseModel{

    private int id;
    private int sticker_category_id;
    private String path;
    private int is_paid;
    private int created_by;
    private String created_date;

    public StickersModel() {
        this.id = 0;
        this.sticker_category_id = 0;
        this.path = "";
        this.is_paid = 0;
        this.created_by = 0;
        this.created_date = "";
    }

    public int getId() {
        return id;
    }

    public boolean setId(int id) {
        this.id = id;
        return true;
    }

    public int getSticker_category_id() {
        return sticker_category_id;
    }

    public boolean setSticker_category_id(int sticker_category_id) {
        this.sticker_category_id = sticker_category_id;
        return true;
    }

    public String getPath() {
        return path;
    }

    public boolean setPath(String path) {
        this.path = StringEscapeUtils.escapeEcmaScript(path);
        return true;
    }

    public int getIs_paid() {
        return is_paid;
    }

    public boolean setIs_paid(int is_paid) {
        this.is_paid = (is_paid<0||is_paid>1)?0:is_paid;
        return true;
    }

    public int getCreated_by() {
        return created_by;
    }

    public boolean setCreated_by(int created_by) {
        this.created_by = created_by;
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
        String query = "INSERT INTO `stickers`( `sticker_category_id`, `path`,`is_paid`, `created_by`) "+
                       " VALUES ("+this.sticker_category_id+",'"+this.path+"',"+this.is_paid+","+this.created_by+")";
        this.insertData(query);
        return this.id;
    }
}
