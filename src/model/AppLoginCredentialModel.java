package model;

import model.datamodel.app.AuthCredential;

import java.sql.SQLException;

/**
 * Created by mi on 10/1/15.
 */
public class AppLoginCredentialModel extends ImageTalkBaseModel{

    private int id;
    private int u_id;
    private String text_status;
    private String phone_number;
    private String access_token;
    private int active;
    private int banned;
    private String created_date;
    public String token;
    public AppLoginCredentialModel(){
        super();
        super.tableName = "app_login_credential";
        this.token = "";
        this.id=0;
        this.u_id=0;
        this.text_status="";
        this.phone_number="";
        this.access_token="";
        this.active=0;
        this.banned=0;
        this.created_date="";
        this.token ="";
    }
    public int getId() {
        return id;
    }

    public boolean setId(int id) {
        this.id = id;
        return true;
    }

    public int getU_id() {
        return u_id;
    }

    public boolean setU_id(int u_id) {
        this.u_id = u_id;
        return true;
    }

    public String getText_status() {
        return text_status;
    }

    public boolean setText_status(String text_status) {
        this.text_status = text_status;
        return true;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public boolean setPhone_number(String phone_number) {
        this.phone_number = phone_number;
        return true;
    }

    public String getAccess_token() {
        return access_token;
    }

    public boolean setAccess_token(String access_token) {
        this.access_token = access_token;
        return true;
    }

    public int getActive() {
        return active;
    }

    public boolean setActive(int active) {
        this.active = active;
        return true;
    }

    public int getBanned() {
        return banned;
    }

    public boolean setBanned(int banned) {
        this.banned = banned;
        return true;
    }

    public String getCreated_date() {
        return created_date;
    }

    public boolean setCreated_date(String created_date) {
        this.created_date = created_date;
        return true;
    }
    public boolean isNumberExist(){
        String query ="select * from " + super.tableName+" where phone_number='"+this.phone_number+"' limit 1";

        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                this.id  = this.resultSet.getInt("id");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }

        return false;
    }
    public AuthCredential getAppCredentialById(){
        AuthCredential authCredential = new AuthCredential();
        String query ="select user_inf.id as user_inf_id," +
                            "user_inf.created_date as user_inf_c_date,user_inf.f_name,user_inf.l_name,user_inf.pic_path," +
                            "app_login_credential.id as app_login_credential_id,app_login_credential.text_status,app_login_credential.access_token,app_login_credential.phone_number," +
                            "app_login_credential.created_date as app_login_credential_c_date  from " + super.tableName+" join user_inf on user_inf.id = app_login_credential.u_id  where app_login_credential.id="+this.id+" limit 1";
        System.out.println(query);
        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                authCredential.id  = this.resultSet.getInt("app_login_credential_id");
                authCredential.textStatus = (this.resultSet.getString("text_status")==null)?"":this.resultSet.getString("text_status");
                authCredential.accessToken = this.resultSet.getString("access_token");
                authCredential.phoneNumber = this.resultSet.getString("phone_number");
                authCredential.user.id = this.resultSet.getInt("user_inf_id");
                authCredential.user.firstName = this.resultSet.getString("f_name");
                authCredential.user.lastName = this.resultSet.getString("l_name");
                authCredential.user.picPath = this.resultSet.getString("pic_path");
                authCredential.user.createdDate = this.resultSet.getString("app_login_credential_c_date");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }

        return authCredential;
    }
    public int insert(){
        if(this.isNumberExist()){
           return 0;
        }
        this.active = 1;
        this.banned = 0;
        this.access_token = this.phone_number+System.nanoTime();


        String query = "INSERT INTO `app_login_credential` "+
        "(`u_id`, `phone_number`, `access_token`, `active`, `banned`)"+
        " VALUES (" +this.u_id+",'"+this.phone_number+"',md5('"+this.access_token+"'),"+this.active+","+this.banned+")";
        System.out.println(query);
        this.id = this.insertData(query);
        return this.id;
    }
}
