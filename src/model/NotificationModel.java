package model;

import com.google.gson.Gson;

/**
 * Created by IntelliJ IDEA.
 * User: rajib
 * Email: rajibcse2k10@gmail.com
 * Date: 11/4/15
 * Project Name:ImageTalk
 */
public class NotificationModel extends ImageTalkBaseModel {

    private int id;
    private String msg;
    private String tag;
    private int is_read;
    private String data_object;
    private String created_date;

    private Gson gson;

    public NotificationModel() {
        this.tableName="notification";

        id=0;
        msg = "";
        tag = "";
        is_read = 0;
        data_object = "";
        created_date = "";

        gson = new Gson();
    }

    public int getId() {
        return id;
    }

    public boolean setId(int id) {
        this.id = id;
        return true;
    }

    public String getMsg() {
        return msg;
    }

    public boolean setMsg(String msg) {
        this.msg = msg;
        return true;
    }

    public String getTag() {
        return tag;
    }

    public boolean setTag(String tag) {
        this.tag = tag;
        return true;
    }

    public int getIs_read() {
        return is_read;
    }

    public boolean setIs_read(int is_read) {
        this.is_read = is_read;
        return true;
    }

    public String getData_object() {
        return data_object;
    }

    public boolean setData_object(String data_object) {
        this.data_object = data_object;
        return true;
    }

    public String getCreated_date() {
        return created_date;
    }

    public boolean setCreated_date(String created_date) {
        this.created_date = created_date;
        return true;
    }

    public int insert()
    {
        String query = "INSERT INTO " + this.tableName + " (`msg`,`tag`,`is_read`,`data_object`,`created_date`) " +
                "VALUES ('"+this.msg+"','"+this.tag+"',"+this.is_read+","+"\""+this.data_object+"\",'"+this.getUtcDateTime()+"')";

        System.out.print(query);
        this.id = this.insertData(query);
        return this.id;
    }
}
