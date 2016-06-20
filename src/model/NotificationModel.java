package model;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import model.datamodel.app.Liker;
import model.datamodel.app.Notification;
import model.datamodel.app.WallPost;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: rajib
 * Email: rajibcse2k10@gmail.com
 * Date: 11/4/15
 * Project Name:ImageTalk
 */
public class NotificationModel extends ImageTalkBaseModel {

    private int id;
    private int owner_id;
    private int person_app_id;
    private int source_id;
    private String source_class;
    private String action_tag;
    private int is_read;
    private String data_object;
    private String created_date;
    private static String[] actionTagNames = {"likepost","addpost","commentPost","tag","commentReply","addContact"};
    private static String[] sourceTagNames = {"wallpost","comment"};

    private Gson gson;

    public NotificationModel() {
        this.tableName="notification";

        id=0;
        owner_id = 0;
        person_app_id = 0;
        source_id = 0;
        source_class = "";
        action_tag = "";
        is_read = 0;
        data_object = "";
        created_date = "";

        gson = new Gson();
    }

    public int getId() {
        return id;
    }

    public void setId(Object id){
        if(!this.setReqParamObj("notification_id",id)){
            return;
        }
        try{
            this.id = Integer.parseInt((String)id);
        }catch (ClassCastException ex){
            System.out.println(ex.getMessage());
            this.setError("id", "id int required");
            this.id = 0;
            return;
        }

    }


    public void setOwnerId(int owner_id) {
        this.owner_id = owner_id;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public void setPerson_app_id(int person_app_id) {
        this.person_app_id = person_app_id;
    }

    public int getPerson_app_id() {
        return person_app_id;
    }

    public void setSource_id(int source_id) {
        this.source_id = source_id;
    }

    public int getSource_id() {
        return source_id;
    }

    public void setSource_class(String source_class) {
        this.source_class = source_class;
    }

    public String getSource_class() {
        return source_class;
    }

    public void setAction_tag(String action_tag) {
        this.action_tag = action_tag;
    }

    public String getAction_tag() {
        return action_tag;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
    }

    public int getIs_read() {
        return is_read;
    }

    public void setData_object(String data_object) {
        this.data_object = data_object;
    }

    public String getData_object() {
        return data_object;
    }

    public ArrayList<Notification> getRecentNotification(){
        ArrayList<Notification> recentNotifications = new ArrayList<Notification>();
        String query =  " SELECT * FROM  `notification` WHERE owner_id=" +this.owner_id;

        query += " order by id DESC ";
        this.offset = this.offset * this.limit;
        query += " LIMIT "+this.offset+" ,"+this.limit+" ";

        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                Notification notification = new Notification();

                String tempDataObject = this.resultSet.getString("data_object");
                if(tempDataObject!=null && !tempDataObject.equals("")){
                    try{
                        notification = this.gson.fromJson(tempDataObject,Notification.class);
                    }catch (Exception ex){
                        System.out.println(ex.getMessage());
                        continue;
                    }
                }
                notification.id  = this.resultSet.getInt("id");
                notification.createdDate  = this.resultSet.getString("created_date");
                notification.isRead = (this.resultSet.getInt("is_read")==0)?false:true;
                recentNotifications.add(notification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return recentNotifications;
    }

    public void insertPostLike()
    {
        WallPostModel wallPostModel = new WallPostModel();
        wallPostModel.setId(this.source_id);

        Notification notification = new Notification();

        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
        appLoginCredentialModel.setId(this.person_app_id);

        notification.person = appLoginCredentialModel.getAppCredentialById();
        notification.actionTag = NotificationModel.actionTagNames[0];
        notification.sourceClass = NotificationModel.sourceTagNames[0];
        notification.isRead = false;
        notification.source = wallPostModel.getById();

        this.setSource_class(notification.sourceClass);
        this.setAction_tag(notification.actionTag);
        this.data_object = this.gson.toJson(notification);

        notification.id = this.insert();

    }

    //post comment notification

    public void insertPostComment()
    {
        WallPostModel wallPostModel = new WallPostModel();
        wallPostModel.setId(this.source_id);

        Notification notification = new Notification();

        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
        appLoginCredentialModel.setId(this.person_app_id);

        notification.person = appLoginCredentialModel.getAppCredentialById();
        notification.actionTag = NotificationModel.actionTagNames[2];
        notification.sourceClass = NotificationModel.sourceTagNames[0];
        notification.isRead = false;
        notification.source = wallPostModel.getById();

        this.setSource_class(notification.sourceClass);
        this.setAction_tag(notification.actionTag);
        this.data_object = this.gson.toJson(notification);

        notification.id = this.insert();

    }


    public void insertPostTag()
    {
        WallPostModel wallPostModel = new WallPostModel();
        wallPostModel.setId(this.source_id);

        Notification notification = new Notification();

        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
        appLoginCredentialModel.setId(this.person_app_id);

        notification.person = appLoginCredentialModel.getAppCredentialById();
        notification.actionTag = NotificationModel.actionTagNames[3];
        notification.sourceClass = NotificationModel.sourceTagNames[0];
        notification.isRead = false;
        notification.source = wallPostModel.getById();

        this.setSource_class(notification.sourceClass);
        this.setAction_tag(notification.actionTag);
        this.data_object = this.gson.toJson(notification);

        notification.id = this.insert();

    }

    public void insertCommentReply()
    {
        WallPostModel wallPostModel = new WallPostModel();
        wallPostModel.setId(this.source_id);

        Notification notification = new Notification();

        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
        appLoginCredentialModel.setId(this.person_app_id);

        notification.person = appLoginCredentialModel.getAppCredentialById();
        notification.actionTag = NotificationModel.actionTagNames[4];
        notification.sourceClass = NotificationModel.sourceTagNames[0];
        notification.isRead = false;
        notification.source = wallPostModel.getById();

        this.setSource_class(notification.sourceClass);
        this.setAction_tag(notification.actionTag);
        this.data_object = this.gson.toJson(notification);

        notification.id = this.insert();

    }

    public void insertAddContact()
    {

        Notification notification = new Notification();

        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
        appLoginCredentialModel.setId(this.person_app_id);

        notification.person = appLoginCredentialModel.getAppCredentialById();
        notification.actionTag = NotificationModel.actionTagNames[5];
        notification.sourceClass = " ";
        notification.isRead = false;
        this.setAction_tag(notification.actionTag);
        this.data_object = this.gson.toJson(notification);

        notification.id = this.insert();

    }


    public boolean updateToRead(){
        String query = "UPDATE " + this.tableName + "  SET is_read=1 WHERE owner_id = "+this.owner_id +" and id="+this.id;
        return this.updateData(query);
    }
    public int insert()
    {
        String query = "INSERT INTO " + this.tableName + " (`owner_id`,`person_app_id`,`source_id`,`source_class`,`action_tag`,`is_read`,`data_object`,`created_date`) " +
                "VALUES("+this.owner_id +","+this.person_app_id+","+this.source_id+",'"+this.source_class+"','"+this.action_tag +"',"+this.is_read+",'"+this.data_object+"','"+this.getUtcDateTime() +"')";

        System.out.println("notification query :" + query);

        this.id = this.insertData(query);
        return this.id;
    }
}
