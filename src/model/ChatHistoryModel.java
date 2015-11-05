package model;

import com.google.gson.Gson;
import model.datamodel.app.Chat;
import org.apache.commons.lang3.StringEscapeUtils;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: rajib
 * Email: rajibcse2k10@gmail.com
 * Date: 11/3/15
 * Project Name:ImageTalk
 */
public class ChatHistoryModel extends ImageTalkBaseModel {

    private long id;
    private String chat_id;
    private int to;
    private int from;
    private String chat_text;
    private String extra;
    private String media_path;
    private int type;
    private String created_date;
    private int read_status;

    private Gson gson;

    public static final int type_txtChat =0 ;
    public static final int type_chatPic =1;
    public static final int type_chatVideo =2 ;

    public ChatHistoryModel() {
        this.tableName = "chat_history";

        id = 0;
        chat_id = "";
        to = 0;
        from = 0;
        chat_text = null;
        extra = null;
        media_path = null;
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
        this.chat_text = StringEscapeUtils.escapeEcmaScript(chat_text.trim());
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

    public String getChat_id() {
        return chat_id;
    }

    public boolean setChat_id(String chat_id) {
        this.chat_id = chat_id;
        return true;
    }
    public long getLatestId(){
        String query = "SELECT id FROM `chat_history` " +
                "WHERE `from` ="+ this.from+" "+
                " ORDER BY id DESC limit 1";


        System.out.println(query);
        this.setQuery(query);
        this.getData();
        try{
            while (this.resultSet.next())
            {
                return  this.resultSet.getLong("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return 0;
    }
    public long insert()
    {
        String query = "INSERT INTO " + this.tableName + " (`chat_id`,`to`,`from`,`chat_text`, `extra`, `media_path`,`type`,`created_date`,`read_status` ) " +
                "VALUES ('"+this.chat_id+"',"+this.to+","+this.from+","+"\""+this.chat_text+"\",'"+this.extra+"','"+this.media_path+"',"+this.type+",'"+this.getUtcDateTime()+"',"+this.read_status+")";
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
        String query = "UPDATE " + this.tableName + " SET `read_status`= 1  WHERE `chat_id`="+this.chat_id;
        return this.updateData(query);
    }
    public ArrayList<Chat> getChatHistory()
    {
        ArrayList<Chat> chatList = new ArrayList<>();
        String query = "SELECT chat_history.* FROM `chat_history` " +
                "WHERE `from` ="+ this.from+" AND `to` ="+ this.to+" OR `from` = "+this.to+" AND `to` = "+this.from+
                " ORDER BY created_date DESC";

        if(this.limit>0)
        {
            this.offset = this.offset * this.limit;
            query += " LIMIT " + this.offset + " ," + this.limit + " ";
        }
        System.out.println(query);
        this.setQuery(query);
        this.getData();
        try{
            while (this.resultSet.next())
            {

                Chat chat = new Chat();
                chat.id = this.resultSet.getLong("chat_history.id");
                chat.chatId =(this.resultSet.getString("chat_history.chat_id")==null)?"":this.resultSet.getString("chat_history.chat_id");
                chat.to = this.resultSet.getInt("chat_history.to");
                chat.from = this.resultSet.getInt("chat_history.from");
                chat.chatText = (this.resultSet.getString("chat_history.chat_text")==null) ? "" : this.resultSet.getString("chat_history.chat_text");
                chat.extra = (this.resultSet.getObject("chat_history.extra")==null)? "" : this.resultSet.getString("chat_history.extra");
                chat.mediaPath = (this.resultSet.getObject("chat_history.media_path")==null)? "" : this.resultSet.getString("chat_history.media_path");
                chat.type = this.resultSet.getInt("chat_history.type");
                try {
                    chat.createdDate = (this.resultSet.getObject("chat_history.created_date") == null)?"":this.getPrcessedTimeStamp(this.resultSet.getTimestamp("chat_history.created_date"));
                }catch(Exception e) {
                    chat.createdDate = "";
                    System.out.println(e.getMessage());
                }
                chat.readStatus = (this.resultSet.getInt("chat_history.read_status")==0)?false:true;

                chatList.add(chat);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return chatList;
    }
    public ArrayList<Chat> getPreviousChatHistory(int duration)
    {
        ArrayList<Chat> previousChatList = new ArrayList<>();

        String query = "SELECT chat_history.* FROM `chat_history` " +
                "WHERE ((`from` ="+ this.from+" AND `to` ="+ this.to+") OR (`from` = "+this.to+" AND `to` = "+this.from+
                ")) AND created_date>=DATE(NOW())-INTERVAL "+duration+" DAY ORDER BY created_date DESC";

        if(this.limit>0)
        {
            this.offset = this.offset * this.limit;
            query += " LIMIT " + this.offset + " ," + this.limit + " ";
        }

        System.out.print(query);
        this.setQuery(query);
        this.getData();
        try{
            while (this.resultSet.next())
            {

                Chat chat = new Chat();
                chat.id = this.resultSet.getLong("chat_history.id");
                chat.chatId =(this.resultSet.getString("chat_history.chat_id")==null)?"":this.resultSet.getString("chat_history.chat_id");
                chat.to = this.resultSet.getInt("chat_history.to");
                chat.from = this.resultSet.getInt("chat_history.from");
                chat.chatText = (this.resultSet.getString("chat_history.chat_text")==null) ? "" : this.resultSet.getString("chat_history.chat_text");
                chat.extra = (this.resultSet.getObject("chat_history.extra")==null)? "" : this.resultSet.getString("chat_history.extra");
                chat.mediaPath = (this.resultSet.getObject("chat_history.media_path")==null)? "" : this.resultSet.getString("chat_history.media_path");
                chat.type = this.resultSet.getInt("chat_history.type");
                try {
                    chat.createdDate = (this.resultSet.getObject("chat_history.created_date") == null)?"":this.getPrcessedTimeStamp(this.resultSet.getTimestamp("chat_history.created_date"));
                }catch(Exception e) {
                    chat.createdDate = "";
                    System.out.println(e.getMessage());
                }
                chat.readStatus = (this.resultSet.getInt("chat_history.read_status")==0)?false:true;

                previousChatList.add(chat);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return previousChatList;
    }
}
