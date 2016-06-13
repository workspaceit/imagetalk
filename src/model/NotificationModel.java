package model;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import model.datamodel.app.Notification;
import model.datamodel.app.WallPost;

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
    Notification notification;
    private static String[] actionTagNames = {"likepost","addpost"};
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
        notification = new Notification();
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


    public void setOwner_id(int owner_id) {
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


    public void insertPostLike()
    {
        WallPostModel wallPostModel = new WallPostModel();
        //Notification notification = new Notification();
        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
        appLoginCredentialModel.setId(this.person_app_id);
        notification.person = appLoginCredentialModel.getAppCredentialById();
        notification.actionTag = NotificationModel.actionTagNames[0];
        notification.sourceClass = NotificationModel.sourceTagNames[0];
        notification.isRead = false;

        wallPostModel.setId(this.source_id);
        notification.source = wallPostModel.getById();


         insert();
    }

    public int insert()
    {
        //AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
        //Notification notification = new Notification();
        //appLoginCredentialModel.setId(this.person_app_id);
        //notification.person = appLoginCredentialModel.getAppCredentialById();

        /*Gson gson = new com.google.gson.Gson();
        com.google.gson.JsonObject tagged = gson.fromJson(tag,JsonElement.class).getAsJsonObject();*/

        Gson gson = new Gson();
        String JsonObj = gson.toJson(notification);

        this.setSource_class(notification.sourceClass);
        this.setAction_tag(notification.actionTag);

        this.setData_object(JsonObj);

        String query = "INSERT INTO " + this.tableName + " (`owner_id`,`person_app_id`,`source_id`,`source_class`,`action_tag`,`is_read`,`data_object`,`created_date`) " +
                "VALUES("+this.owner_id +","+this.person_app_id+","+this.source_id+",'"+this.source_class+"','"+this.action_tag +"',"+this.is_read+",'"+this.data_object+"','"+this.getUtcDateTime() +"')";

        //this.getUtcDateTime();
        System.out.print(query);
        this.id = this.insertData(query);
        return this.id;
        //return  JsonObj;
    }
}
