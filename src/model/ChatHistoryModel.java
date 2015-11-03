package model;

import com.google.gson.Gson;

import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: rajib
 * Email: rajibcse2k10@gmail.com
 * Date: 11/3/15
 * Project Name:ImageTalk
 */
public class ChatHistoryModel extends ImageTalkBaseModel {

    private long id;
    private long chat_id;
    private int to;
    private int from;
    private String chat_text;
    private String extra;
    private String media_path;
    private int type;
    private String created_date;
    private int read_status;

    private Gson gson;

    public ChatHistoryModel() {
        this.tableName = "chat_history";

        id = 0;
        chat_id = 0;
        to = 0;
        from = 0;
        chat_text = "";
        extra = "";
        media_path = "";
        type = 0;
        created_date = "";
        read_status = 0;

        this.gson = new Gson();
    }

    public long getId() {
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

    public int getRead_status() {
        return read_status;
    }

    public boolean setRead_status(int read_status) {
        this.read_status = read_status;
        return true;
    }

    public long getChat_id() {
        return chat_id;
    }

    public boolean setChat_id(long chat_id) {
        this.chat_id = chat_id;
        return true;
    }

    public long insert()
    {
        String query = "INSERT INTO " + this.tableName + " (`chat_id`,`to`,`from`,`chat_text`, `extra`, `media_path`,`type`,`created_date`,`read_status` ) " +
                "VALUES ("+this.chat_id+","+this.to+","+this.from+","+"\""+this.chat_text+"\",'"+this.extra+"','"+this.media_path+"',"+this.type+",'"+this.getUtcDateTime()+"',"+this.read_status+")";
        System.out.print(query);
        this.id = this.insertData(query);
        return this.id;
    }

    public boolean updateReadStatus()
    {
        String query = "UPDATE " + this.tableName + " SET `read_status`='" + this.read_status + "' WHERE `id`="+this.id;
        return this.updateData(query);
    }
    public boolean updateReadStatusBychatId()
    {
        String query = "UPDATE " + this.tableName + " SET `read_status`='" + this.read_status + "' WHERE `chat_id`="+this.chat_id;
        return this.updateData(query);
    }
    public boolean getChatHistory()
    {
        String query = "SELECT `to`, `from`, `chat_text`,`created_date` FROM `chat_history` " +
                "WHERE `from` ="+ this.from+" AND `to` ="+ this.to+" OR `from` = "+this.to+" AND `to` = "+this.from+
                " ORDER BY created_date DESC";
        System.out.println(query);
        this.setQuery(query);
        this.getData();
        try{
            while (this.resultSet.next())
            {
                System.out.print(this.resultSet.getInt("from"));
                System.out.print("  ");
                System.out.print(this.resultSet.getInt("to"));
                System.out.print("  ");
                System.out.print(this.resultSet.getString("chat_text"));
                System.out.print("  ");
                System.out.print(this.resultSet.getString("created_date"));
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean getPreviousChatHistory(int duration)
    {
        String query = "SELECT `to`, `from`, `chat_text`,`created_date` FROM `chat_history` " +
                "WHERE ((`from` ="+ this.from+" AND `to` ="+ this.to+") OR (`from` = "+this.to+" AND `to` = "+this.from+
                ")) AND created_date>=DATE(NOW())-INTERVAL "+duration+" DAY ORDER BY created_date DESC";

        System.out.print(query);
        this.setQuery(query);
        this.getData();
        try{
            while (this.resultSet.next())
            {
                System.out.print(this.resultSet.getInt("from"));
                System.out.print("  ");
                System.out.print(this.resultSet.getInt("to"));
                System.out.print("  ");
                System.out.print(this.resultSet.getString("chat_text"));
                System.out.print("  ");
                System.out.print(this.resultSet.getString("created_date"));
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
