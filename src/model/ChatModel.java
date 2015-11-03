package model;

import com.google.gson.Gson;

/**
 * Created by IntelliJ IDEA.
 * User: rajib
 * Email: rajibcse2k10@gmail.com
 * Date: 11/3/15
 * Project Name:ImageTalk
 */
public class ChatModel extends ImageTalkBaseModel {

    private int id;
    private int to;
    private int from;
    private String chat_text;
    private String extra;
    private String media_path;
    private int type;
    private String created_date;

    private Gson gson;

    public ChatModel() {
        this.tableName = "chat_history";

        id = 0;
        to = 0;
        from = 0;
        chat_text = "";
        extra = "";
        media_path = "";
        type = 0;
        created_date = "";

        this.gson = new Gson();
    }

    public int getId() {
        return id;
    }

    public boolean setId(int id) {
        this.id = id;
        return true;
    }

    public int getTo() {
        return to;
    }

    public boolean setTo(int to) {
        this.to = to;
        return true;
    }

    public int getFrom() {
        return from;
    }

    public boolean setFrom(int from) {
        this.from = from;
        return true;
    }

    public String getChat_text() {
        return chat_text;
    }

    public boolean setChat_text(String chat_text) {
        this.chat_text = chat_text;
        return true;
    }

    public String getExtra() {
        return extra;
    }

    public boolean setExtra(String extra) {
        this.extra = extra;
        return true;
    }

    public String getMedia_path() {
        return media_path;
    }

    public boolean setMedia_path(String media_path) {
        this.media_path = media_path;
        return true;
    }

    public int getType() {
        return type;
    }

    public boolean setType(int type) {
        this.type = type;
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
        String query = "INSERT INTO " + this.tableName + " (`to`,`from`,`chat_text`, `extra`, `media_path`,`type`,`created_date` ) " +
                "VALUES ("+this.to+","+this.from+","+"\""+this.chat_text+"\",'"+this.extra+"','"+this.media_path+"',"+this.type+",'"+this.getUtcDateTime()+"')";
        System.out.print(query);
        this.id = this.insertData(query);
        return this.id;
    }
}
