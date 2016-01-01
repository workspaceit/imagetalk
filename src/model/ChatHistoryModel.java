package model;

import com.google.gson.Gson;
import helper.ImageHelper;
import model.datamodel.app.Chat;
import model.datamodel.app.ChatHistory;
import model.datamodel.app.Contact;
import model.datamodel.app.Places;
import model.datamodel.app.socket.chat.ContactShare;
import model.datamodel.app.socket.chat.PrivateChatPhoto;
import model.datamodel.app.socket.chat.TextChat;
import model.datamodel.photo.Pictures;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

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
    public static final int type_chatLocationShare =3 ;
    public static final int type_chatContactShare =4 ;
    public static final int type_chatPrivatePhoto =5 ;
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

        byte bytes[] = new byte[0];
        try {
            bytes = chat_text.getBytes("UTF-8");
            this.chat_text = new String(bytes, "UTF-8");
            System.out.println("this.chat_text 01" + this.chat_text);
            this.chat_text.replace("\'", "\\\'");
            //this.chat_text = StringEscapeUtils.escapeEcmaScript( this.chat_text.trim());
            System.out.println("\\\'");
            System.out.println("this.chat_text 02" + this.chat_text);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
//        String query = "INSERT INTO " + this.tableName + " (`chat_id`,`to`,`from`,`chat_text`, `extra`, `media_path`,`type`,`created_date`,`read_status` ) " +
//                "VALUES ('"+this.chat_id+"',"+this.to+","+this.from+","+"'"+this.chat_text+"','"+this.extra+"','"+this.media_path+"',"+this.type+",'"+this.getUtcDateTime()+"',"+this.read_status+")";
//       // System.out.print(query);
      //  this.id = this.insertData(query);
        final String query = "INSERT INTO " + this.tableName + " (`chat_id`,`to`,`from`,`chat_text`, `extra`, `media_path`,`type`,`created_date`,`read_status` ) " +
                "VALUES (?,?,?,?,?,?,?,?,?)";

        //this.closeConnection();
        try {
            PreparedStatement ps = null;
            ps = this.con.prepareStatement(query,ps.RETURN_GENERATED_KEYS);

            ps.setString((int) 1, this.chat_id);
            ps.setInt((int) 2, this.to);
            ps.setInt((int) 3, this.from);
            ps.setString((int) 4, this.chat_text);
            ps.setString((int) 5, this.extra);
            ps.setString((int) 6, this.media_path);
            ps.setInt((int) 7, this.type);
            ps.setString((int) 8, this.getUtcDateTime());
            ps.setInt((int) 9, 0);
     //       this.chat_id+"',"+this.to+","+this.from+","+"'"+this.chat_text+"','"+this.extra+"','"+this.media_path+"',"+this.type+",'"+this.getUtcDateTime()+"',"+this.read_status+"
           System.out.println("Query "+ps.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return this.id;
    }

    public boolean updateReadStatus()
    {
        String query = "UPDATE " + this.tableName + " SET `read_status`='" + this.read_status + "' WHERE `id`="+this.id;
        return this.updateData(query);
    }
    public boolean updateReadStatusByChatId()
    {
        String query = "UPDATE " + this.tableName + " SET `read_status`= 1  WHERE `chat_id`="+this.chat_id;
        System.out.println("Query "+query);
        return this.updateData(query);
    }
    public boolean updateIsDeleteTrueById()
    {
        String query = "UPDATE " + this.tableName + " SET `is_deleted`= 1  WHERE `id`="+this.id;
        return this.updateData(query);
    }
    public boolean updateIsDeleteTrueByChat_id()
    {
        String query = "UPDATE " + this.tableName + " SET `is_deleted`= 1  WHERE `chat_id`='"+this.chat_id+"'";
        System.out.println("Query "+query);
        return this.updateData(query);
    }
    public boolean updateIsTakeSnapShotStatusById()
    {
        Chat chat = this.getChatHistoryById();

        PrivateChatPhoto privateChatPhoto =(PrivateChatPhoto)chat.extra;
        privateChatPhoto.tookSnapShot = true;

        this.setExtra(gson.toJson(privateChatPhoto));

        String query = "UPDATE " + this.tableName + " SET `extra`= '"+this.extra+"'  WHERE `id`="+this.id;
        return this.updateData(query);
    }
    public boolean updateIsTakeSnapShotStatusByChatId(String msg,TextChat textChat)
    {
        Chat chat = this.getChatHistoryByChatId();

        PrivateChatPhoto privateChatPhoto =(PrivateChatPhoto)chat.extra;
        privateChatPhoto.tookSnapShot = true;
        privateChatPhoto.extra = msg;
        this.setExtra(gson.toJson(privateChatPhoto));

        String query = "UPDATE " + this.tableName + " SET `extra`= '"+this.extra+"'  WHERE `id`='"+chat.id+"'";

        textChat.extra = privateChatPhoto;

        this.chat_id = textChat.chatId;
        this.to = privateChatPhoto.to;
        this.from =privateChatPhoto.from;
        this.extra = this.gson.toJson(textChat.extra);
        this.chat_text = textChat.text;
        this.type = 6;
        this.dbConnectionRecheck();
        this.insert();


        return this.updateData(query);
    }
    public boolean insertTakeSnapShotStatusByChatId(String msg)
    {
        Chat chat = this.getChatHistoryById();


        PrivateChatPhoto privateChatPhoto =(PrivateChatPhoto)chat.extra;
        privateChatPhoto.tookSnapShot = true;
        privateChatPhoto.extra = msg;
        this.setExtra(gson.toJson(privateChatPhoto));

        String query = "UPDATE " + this.tableName + " SET `extra`= '"+this.extra+"'  WHERE `chat_id`='"+this.chat_id+"'";
        return this.updateData(query);
    }
    public boolean updateCountDownByChatId(int timer)
    {
        Chat chat = this.getChatHistoryByChatId();
        PrivateChatPhoto privateChatPhoto =(PrivateChatPhoto)chat.extra;
        privateChatPhoto.timer = timer;

        this.setExtra(gson.toJson(privateChatPhoto));

        String query = "UPDATE " + this.tableName + " SET `extra`= '"+this.extra+"'  WHERE `id`='"+chat.id+"'";
        return this.updateData(query);
    }
    public boolean updateMediaById()
    {
        String query = "UPDATE " + this.tableName + " SET `is_deleted`= 1  WHERE `id`="+this.id;
        return this.updateData(query);
    }
    public boolean deleteById()
    {
        String query = "DELETE FROM " + this.tableName + "  WHERE `id`="+this.id;
        return this.updateData(query);
    }
    public ArrayList<Chat> getChatHistory()
    {
        ArrayList<Chat> chatList = new ArrayList<>();
        String query = "SELECT chat_history.* FROM `chat_history` " +
                "WHERE ( `from` ="+ this.from+" AND `to` ="+ this.to+" OR `from` = "+this.to+" AND `to` = "+this.from+" ) "+
                " AND is_deleted = 0 ORDER BY id DESC";

        if(this.limit>0)
        {
            this.offset = this.offset * this.limit;
            query += " LIMIT " + this.offset + " ," + this.limit + " ";
        }

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
                String tmpString = (this.resultSet.getObject("chat_history.chat_text")==null) ? "" : this.resultSet.getString("chat_history.chat_text");
                System.out.println("chat_history.chat_text");
                System.out.println(tmpString);
                chat.chatText = new String(tmpString.getBytes("UTF-8"),"UTF-8");
                //chat.chatText = (this.resultSet.getString("chat_history.chat_text")==null) ? "" : this.resultSet.getString("chat_history.chat_text");
                chat.type = this.resultSet.getInt("chat_history.type");

                if(chat.type==3) {
                    chat.extra = (this.resultSet.getObject("chat_history.extra") == null
                            || this.resultSet.getString("chat_history.extra").equals("null")) ? new Object() : this.gson.fromJson(this.resultSet.getString("chat_history.extra"), Places.class);
                }else if(chat.type==4) {
                    chat.extra = (this.resultSet.getObject("chat_history.extra") == null
                            || this.resultSet.getString("chat_history.extra").equals("null")) ? new Object() : this.gson.fromJson(this.resultSet.getString("chat_history.extra"), Contact.class);
                }else if(chat.type==5){
                    chat.extra = (this.resultSet.getObject("chat_history.extra") == null
                            || this.resultSet.getString("chat_history.extra").equals("null")) ? new Object() : this.gson.fromJson(this.resultSet.getString("chat_history.extra"), PrivateChatPhoto.class);
                }


                if(chat.type==1 || chat.type==5){
                    chat.mediaPath = (this.resultSet.getObject("chat_history.media_path")==null
                            || this.resultSet.getString("chat_history.media_path").equals("null"))? new Object() : this.gson.fromJson(this.resultSet.getString("chat_history.media_path"), Pictures.class);
                }


                try {
                    chat.createdDate = (this.resultSet.getObject("chat_history.created_date") == null) ? "" : this.getUTCTimeStamp(this.resultSet.getString("chat_history.created_date"));
                }catch(Exception e) {
                    chat.createdDate = "";
                    System.out.println(e.getMessage());
                }
                chat.readStatus = (this.resultSet.getInt("chat_history.read_status")==0)?false:true;

                chatList.add(chat);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }

        Collections.reverse(chatList);
        return chatList;
    }
    public Chat getChatHistoryById()
    {
        Chat chat = new Chat();
        String query = "SELECT chat_history.* FROM `chat_history` " +
                "WHERE id = "+this.id;
        this.setQuery(query);
        System.out.println(query);
        this.getData();
        try{
            while (this.resultSet.next())
            {


                chat.id = this.resultSet.getLong("chat_history.id");
                chat.chatId =(this.resultSet.getString("chat_history.chat_id")==null)?"":this.resultSet.getString("chat_history.chat_id");
                chat.to = this.resultSet.getInt("chat_history.to");
                chat.from = this.resultSet.getInt("chat_history.from");
                String tmpString = (this.resultSet.getObject("chat_history.chat_text")==null) ? "" : this.resultSet.getString("chat_history.chat_text");

                chat.chatText = new String(tmpString.getBytes("UTF-8"),"UTF-8");
                chat.type = this.resultSet.getInt("chat_history.type");

                if(chat.type==3) {
                    chat.extra = (this.resultSet.getObject("chat_history.extra") == null
                            || this.resultSet.getString("chat_history.extra").equals("null")) ? new Object() : this.gson.fromJson(this.resultSet.getString("chat_history.extra"), Places.class);
                }else if(chat.type==4) {
                    chat.extra = (this.resultSet.getObject("chat_history.extra") == null
                            || this.resultSet.getString("chat_history.extra").equals("null")) ? new Object() : this.gson.fromJson(this.resultSet.getString("chat_history.extra"), Contact.class);
                }else if(chat.type==5){
                    chat.extra = (this.resultSet.getObject("chat_history.extra") == null
                            || this.resultSet.getString("chat_history.extra").equals("null")) ? new Object() : this.gson.fromJson(this.resultSet.getString("chat_history.extra"), PrivateChatPhoto.class);
                }
                if(chat.type==1 || chat.type==5){
                    chat.mediaPath = (this.resultSet.getObject("chat_history.media_path")==null
                            || this.resultSet.getString("chat_history.media_path").equals("null"))? new Object() : this.gson.fromJson(this.resultSet.getString("chat_history.media_path"), Pictures.class);
                }
                try {
                    chat.createdDate = (this.resultSet.getObject("chat_history.created_date") == null) ? "" : this.getUTCTimeStamp(this.resultSet.getString("chat_history.created_date"));
                }catch(Exception e) {
                    chat.createdDate = "";
                    System.out.println(e.getMessage());
                }
                chat.readStatus = (this.resultSet.getInt("chat_history.read_status")==0)?false:true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }


        return chat;
    }
    public Chat getChatHistoryByChatId()
    {
        Chat chat = new Chat();
        String query = "SELECT chat_history.* FROM `chat_history` " +
                "WHERE chat_id = "+this.chat_id;
        this.setQuery(query);
        System.out.println(query);
        this.getData();
        try{
            while (this.resultSet.next())
            {


                chat.id = this.resultSet.getLong("chat_history.id");
                chat.chatId =(this.resultSet.getString("chat_history.chat_id")==null)?"":this.resultSet.getString("chat_history.chat_id");
                chat.to = this.resultSet.getInt("chat_history.to");
                chat.from = this.resultSet.getInt("chat_history.from");
                String tmpString = (this.resultSet.getObject("chat_history.chat_text")==null) ? "" : this.resultSet.getString("chat_history.chat_text");

                chat.chatText = new String(tmpString.getBytes("UTF-8"),"UTF-8");
                chat.type = this.resultSet.getInt("chat_history.type");

                if(chat.type==3) {
                    chat.extra = (this.resultSet.getObject("chat_history.extra") == null
                            || this.resultSet.getString("chat_history.extra").equals("null")) ? new Object() : this.gson.fromJson(this.resultSet.getString("chat_history.extra"), Places.class);
                }else if(chat.type==4) {
                    chat.extra = (this.resultSet.getObject("chat_history.extra") == null
                            || this.resultSet.getString("chat_history.extra").equals("null")) ? new Object() : this.gson.fromJson(this.resultSet.getString("chat_history.extra"), Contact.class);
                }else if(chat.type==5){
                    chat.extra = (this.resultSet.getObject("chat_history.extra") == null
                            || this.resultSet.getString("chat_history.extra").equals("null")) ? new Object() : this.gson.fromJson(this.resultSet.getString("chat_history.extra"), PrivateChatPhoto.class);
                }
                if(chat.type==1 || chat.type==5){
                    chat.mediaPath = (this.resultSet.getObject("chat_history.media_path")==null
                            || this.resultSet.getString("chat_history.media_path").equals("null"))? new Object() : this.gson.fromJson(this.resultSet.getString("chat_history.media_path"), Pictures.class);
                }
                try {
                    chat.createdDate = (this.resultSet.getObject("chat_history.created_date") == null) ? "" : this.getUTCTimeStamp(this.resultSet.getString("chat_history.created_date"));
                }catch(Exception e) {
                    chat.createdDate = "";
                    System.out.println(e.getMessage());
                }
                chat.readStatus = (this.resultSet.getInt("chat_history.read_status")==0)?false:true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }


        return chat;
    }
    public ArrayList<Chat> getChatHistory(int myId,int receiver)
    {
        ArrayList<Chat> chatList = new ArrayList<>();
        String query = "SELECT chat_history.* FROM `chat_history` " +
                "WHERE `from` ="+ myId+" AND `to` ="+ receiver+" OR `from` = "+receiver+" AND `to` = "+myId+
                " AND is_deleted = 0  ORDER BY ID DESC LIMIT 1";


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


                String tmpString = (this.resultSet.getObject("chat_history.chat_text")==null) ? "" : this.resultSet.getString("chat_history.chat_text");

                chat.chatText = new String(tmpString.getBytes("UTF-8"),"UTF-8");

                chat.extra = (this.resultSet.getObject("chat_history.extra")==null
                        || this.resultSet.getString("chat_history.extra").toString().equals("null"))? new Object() : this.resultSet.getString("chat_history.extra");
                chat.type = this.resultSet.getInt("chat_history.type");

                if(chat.type==3) {
                    chat.extra = (this.resultSet.getObject("chat_history.extra") == null
                            || this.resultSet.getString("chat_history.extra").equals("null")) ? new Object() : this.gson.fromJson(this.resultSet.getString("chat_history.extra"), Places.class);
                }else if(chat.type==4) {
                    chat.extra = (this.resultSet.getObject("chat_history.extra") == null
                            || this.resultSet.getString("chat_history.extra").equals("null")) ? new Object() : this.gson.fromJson(this.resultSet.getString("chat_history.extra"), Contact.class);
                }else if(chat.type==5){
                    chat.extra = (this.resultSet.getObject("chat_history.extra") == null
                            || this.resultSet.getString("chat_history.extra").equals("null")) ? new Object() : this.gson.fromJson(this.resultSet.getString("chat_history.extra"), PrivateChatPhoto.class);
                }


                if(chat.type==1 || chat.type==5){
                    chat.mediaPath = (this.resultSet.getObject("chat_history.media_path")==null
                            || this.resultSet.getString("chat_history.media_path").equals("null"))? new Object() : this.gson.fromJson(this.resultSet.getString("chat_history.media_path"), Pictures.class);
                }

                try {
                    chat.createdDate = (this.resultSet.getObject("chat_history.created_date") == null)?"":this.getUTCTimeStamp(this.resultSet.getString("chat_history.created_date"));
                }catch(Exception e) {
                    chat.createdDate = "";
                    System.out.println(e.getMessage());
                }
                chat.readStatus = (this.resultSet.getInt("chat_history.read_status")==0)?false:true;

                chatList.add(chat);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }
        return chatList;
    }
    public int getChatUnreadHistory(int myId,int receiver)
    {

        String query = "SELECT count(chat_history.id) as historyCount FROM `chat_history` " +
                " WHERE `from` = "+receiver+" AND `to` = "+myId+
                " AND read_status = 0 ORDER BY ID DESC LIMIT 11";


        System.out.println(query);
        this.setQuery(query);
        this.getData();
        try{
            while (this.resultSet.next())
            {
                return this.resultSet.getInt("historyCount");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return 0;
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
                String tmpString = (this.resultSet.getObject("chat_history.chat_text")==null) ? "" : this.resultSet.getString("chat_history.chat_text");

                chat.chatText = new String(tmpString.getBytes("UTF-8"),"UTF-8");
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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }
        return previousChatList;
    }

    public ArrayList<ChatHistory> getChatsWithContact(int myId)
    {
        ArrayList<ChatHistory> chatWithContactArrayList = new ArrayList<>();

        /*String query = "SELECT "+this.tableName+".* " +
                "FROM "+this.tableName+
                " WHERE id IN " +
                "(SELECT MAX(id) AS id FROM (" +
                "SELECT id, `from` AS id_with FROM "+this.tableName+" WHERE `to` = "+this.from+
                " UNION ALL " +
                "SELECT id, `to` AS id_with FROM "+this.tableName+" WHERE `from` = "+this.from+") t " +
                "GROUP BY id_with)";*/

        String query = "SELECT IF (`from` = "+this.from+", `to`, `from`) AS recipients"+
        " FROM "+this.tableName+
        " WHERE "+this.from+" IN (`from`, `to`)"+
        " GROUP BY recipients"+
        " ORDER BY MAX(id) DESC";

        System.out.println(query);
        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next())
            {
                ChatHistory chatHistory = new ChatHistory();

                int recipient = this.resultSet.getInt("recipients");
                System.out.println(recipient);

                ContactModel contactmodel = new ContactModel();
                Contact tempContact = new Contact();



                tempContact =  contactmodel.getContactByOwnerId(this.from,recipient);
                chatHistory.contact = (tempContact.id==0)?contactmodel.getContactByByContactId(recipient):tempContact;

                ChatHistoryModel chatHistoryModel = new ChatHistoryModel();
                ArrayList<Chat> chats = chatHistoryModel.getChatHistory(myId,recipient);

                chatHistory.chat = chats;
                chatHistory.unRead = chatHistoryModel.getChatUnreadHistory(myId,recipient);

                chatWithContactArrayList.add(chatHistory);

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }



    return chatWithContactArrayList;
    }

}
